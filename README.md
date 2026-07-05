# 🌱 Fertilizer Recommendation Agent

An intelligent, full-stack AI agent that recommends the best fertilizer based on crop type, soil nutrients, weather conditions, and farmer goals. Powered by Spring Boot, React, MongoDB, ChromaDB, and Groq Llama-3.3 API.

🌐 **Live Deployment Link:** [https://ai-fertilizer-agent-4.onrender.com/](https://ai-fertilizer-agent-4.onrender.com/)


## 🚀 Features
- **AI-Powered Recommendations:** Analyzes NPK levels, soil pH, and weather data to provide explainable fertilizer recommendations.
- **RAG Architecture:** Uses ChromaDB as a vector database for retrieving specific agricultural knowledge before prompting Gemini.
- **Modern UI:** Built with React, featuring a responsive, green-gradient glassmorphism dashboard.
- **Containerized:** Ready to deploy via Docker to AWS EC2 or Azure App Service.

## 🛠 Tech Stack
- **Frontend:** React, Vite, Vanilla CSS
- **Backend:** Spring Boot (Java 17), Maven
- **Databases:** MongoDB (Transaction/User data), ChromaDB (Vector Knowledge Base)
- **AI Integration:** Google Gemini API (LLM), Prompt Engineering with Few-Shot examples
- **Infrastructure:** Docker, Docker Compose

## ⚙️ Installation & Setup

1. **Clone the repository**
2. **Setup Environment Variables:**
   Create a `.env` file or export the following variables:
   ```bash
   GEMINI_API_KEY=your_gemini_api_key_here
   OPENWEATHERMAP_API_KEY=your_weather_api_key_here
   ```

3. **Run with Docker Compose (Recommended):**
   This spins up MongoDB and ChromaDB.
   ```bash
   docker-compose up -d
   ```

4. **Start the Backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. **Start the Frontend:**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 🏗 Architecture & Flow Diagram

The project uses a classic **Three-Tier Architecture** coupled with a **Retrieval-Augmented Generation (RAG)** pipeline to supply domain-specific agricultural context to the LLM.

For a detailed view of the components and request sequence, please read the [Detailed Flow & Architecture Document](docs/architecture_flow.md).

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
    RS -->|3. Save raw query| Mongo
    RS -->|4. Generate embedding if available| GS
    RS -->|5. Query similar documents| CDBS
    CDBS <-->|Query embeddings & Fetch docs| Chroma
    RS -->|6. Compile prompt + context| PES
    RS -->|7. Send compiled prompt| GS
    GS <-->|Invoke Chat Completion API| GroqAPI
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
```

