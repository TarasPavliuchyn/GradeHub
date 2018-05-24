Required software:
- Java 8
- Gradle 4.7

For test execute:
   - gradle test
For build execute:
   - gradle build
For application run execute:
   - gradle bootRun

Application starts on http:\\localhost:8080
Authentication is required, except root URL

There are two default users:
Login | Password |
-----------------|
user  | password |
-----------------|
admin | password |

"user" can only read data, "admin" can do all CRUD operations.

Endpoints & HTTP operations:
    ● /organizations/{orgId}/courses (GET)
    ● /organizations/{orgId}/courses/{courseId} (GET, POST, PATCH, DELETE)
    ● /organizations/{orgId}/courses/{courseId}/exams (GET)
    ● /organizations/{orgId}/courses/{courseId}/exams/{examId} (GET, DELETE)