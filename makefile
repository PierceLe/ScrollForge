serve-backend:
	@cd backend && docker compose up --build

serve-frontend:
	@cd frontend && docker compose up --build

run-test:
	@cd backend && ./gradlew build test JacocoTestReport

serve-frontend-locally:
	@cd frontend && npm install && npm run generate-css-types && npm run dev
