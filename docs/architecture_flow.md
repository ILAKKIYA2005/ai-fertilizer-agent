# 🌱 Agriculture Fertilizer Recommendation Agent - Flow & Architecture

This document provides a detailed breakdown of the system architecture, component structures, and data flows within the **Agriculture Fertilizer Recommendation Agent**.

---

## 🏗 System Architecture Diagram

The system follows a classic **Three-Tier Architecture** coupled with a **Retrieval-Augmented Generation (RAG)** pipeline to supply domain-specific agricultural context to the LLM.

```mermaid
graph TD
    %% Styling definitions
    classDef client fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#1b5e20;
    classDef server fill:#e3f2fd,stroke:#1565c0,stroke-width:2px,color:#0d47a1;
    classDef database fill:#efebe9,stroke:#4e342e,stroke-width:2px,color:#3e2723;
    classDef ai fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px,color:#4a148c;

    %% Nodes
    subgraph Frontend [Client Application - React/Vite]
        UI[Landing Page & Dashboard UI]:::client
        ChatUI[AI Chat Interface]:::client
        AdminUI[Admin Dashboard]:::client
    end

    subgraph Backend [Backend API - Spring Boot]
        RC[Recommendation Controller]:::server
        CC[Chat Controller]:::server
        AC[Admin Controller]:::server
        
        RS[Recommendation Service]:::server
        CS[Chat Service]:::server
        PES[Prompt Engineering Service]:::server
        GS[Groq Service]:::server
        CDBS[ChromaDB Service]:::server
    end

    subgraph Storage [Data & Knowledge Bases]
        Mongo[(MongoDB)]:::database
        Chroma[(ChromaDB Vector Store)]:::database
    end

    subgraph AIAPI [AI Infrastructure]
        GroqAPI[Groq API / Llama-3.3]:::ai
    end

    %% Flows & Interactions
    UI -->|1. Submit Farmer Query POST /api/recommend| RC
    RC -->|2. Delegate processing| RS
    
    %% Save query to Mongo
    RS -->|3. Save raw query| Mongo
    
    %% Vector database query for context
    RS -->|4. Generate embedding if available| GS
    RS -->|5. Query similar documents| CDBS
    CDBS <-->|Query embeddings & Fetch docs| Chroma
    
    %% Prompt assembly
    RS -->|6. Compile prompt + context| PES
    
    %% LLM Execution
    RS -->|7. Send compiled prompt| GS
    GS <-->|Invoke Chat Completion API| GroqAPI
    
    %% Response storage & return
    RS -->|8. Parse JSON response & Save| Mongo
    RS -->|9. Return Recommendation JSON| RC
    RC -->|10. Render formatted recommendation| UI

    %% Chat flows
    ChatUI -->|POST /api/chat/message| CC
    CC -->|Process Message| CS
    CS -->|Fetch chat history & Save user msg| Mongo
    CS -->|Generate chat response| GS
    CS -->|Save agent response| Mongo
    CS -->|Return ChatHistory| CC
    CC -->|Display chat history| ChatUI

    %% Admin flows
    AdminUI -->|GET /api/admin/analytics| AC
    AdminUI -->|POST /api/admin/rebuild-chroma| AC
    AC -->|Read stats| Mongo
    AC -->|Rebuild / Seed docs| CDBS
```

---

## 🔄 Sequence Diagram: Fertilizer Recommendation Flow

The diagram below details the chronological sequence of requests, processing steps, database operations, and external API requests executed when a farmer requests a new recommendation.

```mermaid
sequenceDiagram
    autonumber
    actor Farmer as Farmer / Client (React UI)
    participant RC as RecommendationController
    participant RS as RecommendationService
    participant Mongo as MongoDB
    participant Chroma as ChromaDB
    participant GS as GroqService
    participant LLM as Groq API (Llama 3.3)

    Farmer->>RC: POST /api/recommend (FarmerQuery)
    activate RC
    RC->>RS: processQuery(query)
    activate RS
    
    RS->>Mongo: Save FarmerQuery (for analytics and history)
    Note over RS,Chroma: Step A: Retrieval-Augmented Generation (RAG) Context
    RS->>GS: generateEmbedding(crop + problem)
    GS-->>RS: embedding vector (if supported)
    RS->>Chroma: searchSimilar(embedding, limit=3)
    Chroma-->>RS: matching agricultural docs (context)
    
    Note over RS,LLM: Step B: AI Recommendation Generation
    RS->>RS: Build structured prompt (query + context + few-shot rules)
    RS->>GS: generateRecommendation(prompt)
    activate GS
    GS->>LLM: POST /chat/completions (model: llama-3.3-70b-versatile)
    LLM-->>GS: AI response (Structured JSON containing recommendations)
    GS-->>RS: Raw AI response
    deactivate GS
    
    RS->>RS: Parse JSON (deserialize to Recommendation model)
    RS->>Mongo: Save Recommendation object (referenced to Query)
    RS-->>RC: Recommendation
    deactivate RS
    RC-->>Farmer: HTTP 200 OK (Recommendation JSON)
    deactivate RC
```

---

## 🛠 Component Roles & Responsibilities

### 1. Presentation Layer (React + Vite)
- **`LandingPage.jsx`**: Welcomes the user and routes them to the dashboard.
- **`Dashboard.jsx`**: The main interface collecting agricultural telemetry:
  - Crop Details (Name, Stage of growth)
  - Soil Nutrients (NPK, pH, Moisture)
  - Weather & General Info (Location, Weather conditions, Problems observed)
  - Goal Selection (Max Yield, Safe/Minimum Cost, Soil Health improvement)
  - Interactive chatbot helper to address follow-up farming queries.
- **`AdminPanel.jsx`**: Allows administrators to view usage analytics, view user registrations, and trigger ChromaDB vector store rebuilds.

### 2. Controller / Web Layer (Spring Boot RestControllers)
- **`RecommendationController`**: Exposes `/api/recommend` endpoint for posting fertilizer query telemetry.
- **`ChatController`**: Manages conversational message endpoints (`/api/chat/message`) and fetches session chat histories (`/api/chat/history/{sessionId}`).
- **`AdminController`**: Exposes metrics and administrative tools such as rebuilding database search indexes.

### 3. Application Services (Spring Boot Services)
- **`RecommendationService`**: Orchestrates the full pipeline of saving user input, fetching similar documents, prompt composition, model inference, and output formatting.
- **`ChatService`**: Pulls conversational context, posts new dialogue nodes, calls LLM services, and stores dialogue runs.
- **`ChromaDBService`**: Acts as an HTTP gateway to the Vector DB to create collections, upsert document collections, and run cosine similarity search.
- **`GroqService`**: Standardizes payloads sent to Groq endpoints for chat text generation.
- **`PromptEngineeringService`**: Encapsulates few-shot engineering techniques, system instructions, and schemas to ensure the model responds with clean, structured JSON payloads.

---

## 💾 Storage & Data Schemas

The backend interacts with two distinct database instances defined under `docker-compose.yml`:

### MongoDB Collections
1. **`User`**: Stored user accounts/profiles.
2. **`FarmerQuery`**: Structured user queries capturing crop type, NPK, and environmental variables.
3. **`Recommendation`**: Generated outcomes containing application quantities, timings, organic alternatives, and predicted yield improvements.
4. **`ChatHistory`**: Session-bound conversational state mapping the sequence of questions and agent answers.

### ChromaDB Collections
- **`fertilizer_knowledge`**: Document vector index housing agricultural instructions and expert guidelines utilized during the RAG lookup phase.
