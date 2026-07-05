# 🌱 Fertilizer Recommendation Agent

An intelligent, full-stack AI agent that recommends the best fertilizer based on crop type, soil nutrients, weather conditions, and farmer goals. Powered by Spring Boot, React, MongoDB, ChromaDB, and Google Gemini API.

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

## 🏗 Architecture
- **Frontend (React)** sends farmer data via REST to the Backend.
- **Backend (Spring Boot)** saves the query to MongoDB, creates an embedding, and queries ChromaDB for relevant agricultural documents.
- **AI Engine** constructs a detailed prompt combining the farmer's query, ChromaDB context, and few-shot examples, and sends it to the Gemini API.
- The structured JSON response is parsed, stored in MongoDB, and sent back to the React UI for beautiful presentation.
