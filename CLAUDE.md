# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

"Lerne dem Llama lesen" is a Quarkus CLI application that processes handwritten PDF forms using LLM analysis. The application extracts images from PDF documents, sends them to Google Vertex AI for analysis, and stores the structured results in a PostgreSQL database.

## Architecture

The application follows hexagonal architecture principles:

### Domain Layer (`src/main/java/com/gepardec/lernelamalesen/domain/`)
- **Models**: `FormData`, `DocumentImage`, `LlmRequest`, `LlmResponse`
- **Ports**: `PdfProcessorPort`, `LlmPort`, `FormDataRepositoryPort`

### Application Layer (`src/main/java/com/gepardec/lernelamalesen/application/`)
- **Services**: `DocumentProcessingService`, `PromptService`

### Infrastructure Layer (`src/main/java/com/gepardec/lernelamalesen/infrastructure/`)
- **Adapters**: 
  - `PdfProcessorAdapter` (PDFBox integration)
  - `VertexAiLlmAdapter` (Google Vertex AI integration)
  - `FormDataRepositoryAdapter` (PostgreSQL integration)
- **CLI**: `DocumentProcessorCommand` (Picocli integration)

## Common Commands

### Build and Run
```bash
# Compile the application
./mvnw compile

# Run in development mode
./mvnw quarkus:dev

# Build native executable
./mvnw package -Pnative

# Run tests
./mvnw test

# Run with specific profile
./mvnw quarkus:dev -Dquarkus.profile=dev
```

### CLI Usage
```bash
# Process a PDF file
java -jar target/quarkus-app/quarkus-run.jar -f path/to/document.pdf

# Process with verbose output
java -jar target/quarkus-app/quarkus-run.jar -f path/to/document.pdf -v

# Show help
java -jar target/quarkus-app/quarkus-run.jar --help
```

## Configuration

### Required Environment Variables
- `DB_URL`: PostgreSQL database URL
- `DB_USER`: Database username  
- `DB_PASSWORD`: Database password
- `VERTEX_AI_PROJECT_ID`: Google Cloud project ID
- `VERTEX_AI_ACCESS_TOKEN`: Google Cloud access token

### Optional Configuration
- `VERTEX_AI_LOCATION`: Vertex AI region (default: us-central1)
- `VERTEX_AI_MODEL`: Model to use (default: gemini-1.5-pro-vision-001)

## Database

The application uses PostgreSQL with Hibernate ORM and Panache. Database migrations are in `src/main/resources/db/migration/`.

## Testing

- Unit tests use JUnit 5 and Mockito
- Integration tests use H2 in-memory database
- Test configuration in `application.yml` under `%test` profile