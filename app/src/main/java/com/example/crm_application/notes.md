CRM Mobile App Development Notes

GitHub Repo: AdarshVishwakarma12/crm-mobile-application

Project Overview

You are building a mobile CRM app (Android) that connects with your existing Django backend hosted at:
https://ctrlcrm.pythonanywhere.com

Tech Stack:
•	Android (Kotlin)
•	Django backend (SQLite for now)
•	REST APIs using Retrofit and OkHttp

Features and Progress
•	App Architecture
•	bottom navigation implemented
•	bottom sheet for account section implemented
•	frame layout used for fragment container
•	Authentication
•	Django backend JWT login working
•	login screen implemented in Android
•	access and refresh tokens stored using SharedPreferences
•	token validation on app start implemented
•	logout button implemented to clear tokens and redirect to login
•	Fragment Navigation
•	fragments created: Dashboard, Leads, Tasks, Roles
•	bottom navigation switching between fragments implemented
•	ViewModel used to load data from API
•	Retrofit successfully fetching data and logging in console
•	Retrofit Integration
•	base URL configured: https://ctrlcrm.pythonanywhere.com
•	Gson converter added to Retrofit builder
•	OkHttp logging interceptor added for debugging
•	token-based authorization handled by appending “Authorization: Bearer <access_token>” to headers
•	all API calls made from ViewModel layer
•	GET and POST requests implemented with proper error logging
•	results are collected and can be passed to UI via LiveData or StateFlow
•	API Testing with Curl
•	login:

curl -X POST https://ctrlcrm.pythonanywhere.com/api/token/ \
-H "Content-Type: application/json" \
-d '{"email": "user@example.com", "password": "password123"}'


	•	leads list:

curl -X GET https://ctrlcrm.pythonanywhere.com/api/leads/ \
-H "Authorization: Bearer <access_token>"


	•	task creation:

curl -X POST https://ctrlcrm.pythonanywhere.com/api/tasks/ \
-H "Authorization: Bearer <access_token>" \
-H "Content-Type: application/json" \
-d '{"title": "Follow up", "description": "Call client"}'



Pending Tasks
•	Django Token Integration
•	create ViewModel method to POST email/password to /api/token/
•	parse access and refresh token from response
•	store them securely using EncryptedSharedPreferences or Keystore
•	Logout Improvements
•	implement logout button logic to clear stored tokens
•	navigate to login screen after logout
•	UX Improvements
•	add error messages on login failure
•	show loading indicators for auth and API requests
•	handle edge cases like token expiration or network failure
•	Database Consideration
•	SQLite being used currently
•	upgrade to PostgreSQL when scaling is needed (recommended before production deployment)
•	Optional Enhancements
•	implement Room database for local caching
•	add push notification support

Notes
•	verify all API endpoints using curl before integrating in Retrofit
•	make sure headers and payloads match the backend expectations
•	use logcat to inspect OkHttp interceptor logs for debugging API responses

Next Steps
•	send login request to Django using Retrofit
•	store JWT tokens on success
•	use access token to authorize further API calls like fetching leads or creating tasks