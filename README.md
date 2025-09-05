# Lerne dem Llama lesen

A Quarkus CLI application that processes handwritten PDF forms using LLM analysis to extract structured data.

## Overview

This application automates the process of digitizing handwritten forms by:
1. Extracting images from PDF documents
2. Analyzing handwritten content using Google Vertex AI
3. Storing structured data in PostgreSQL database

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL database
- Google Cloud project with Vertex AI API enabled

## Quick Start

### 1. Environment Setup

```bash
export DB_URL="jdbc:postgresql://localhost:5432/lerne_lama_db"
export DB_USER="lerne-lama"
export DB_PASSWORD="your-secure-password"
export VERTEX_AI_PROJECT_ID="your-gcp-project"
export VERTEX_AI_LOCATION="us-central1"
export VERTEX_AI_MODEL="gemini-1.5-pro-vision-001"
export VERTEX_AI_CREDENTIALS_PATH="/path/to/service-account-key.json"
```

**Important**: Never commit service account keys or credentials to version control.

### 2. Build and Run

```bash
# Build the application
./mvnw clean package

# Run in development mode
./mvnw quarkus:dev

# Process a PDF file
java -jar target/quarkus-app/quarkus-run.jar -f document.pdf
```

## Usage

```bash
# Basic usage
java -jar target/quarkus-app/quarkus-run.jar -f path/to/form.pdf

# With verbose output
java -jar target/quarkus-app/quarkus-run.jar -f path/to/form.pdf -v

# Show help
java -jar target/quarkus-app/quarkus-run.jar --help
```

## Configuration

Key configuration options in `application.yml`:

- **Database**: PostgreSQL connection settings
- **Vertex AI**: Model configuration and authentication
- **Prompts**: System and user prompts for LLM analysis

## Architecture

Built with hexagonal architecture:
- **Domain**: Core business models and ports
- **Application**: Business logic and services  
- **Infrastructure**: External adapters (database, LLM, CLI)

## Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## Development

The application uses:
- Quarkus framework
- PDFBox for PDF processing
- Google Vertex AI for document analysis
- PostgreSQL with Hibernate ORM
- PicoCLI for command-line interface