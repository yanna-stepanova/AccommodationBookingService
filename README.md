<h1 style="color: #5e9ca0;">AccommodationBookingService</h1>
<p>This project is an online accommodation booking system. Users can browse and book different types of accommodations such as apartments, guest houses, and more. The system is built using the Spring Boot framework and follows the MVC architecture. The backend is supported by a PostgreSQL database and uses Liquibase for schema management. All operations are accessible via RESTful API with integrated Swagger documentation, providing a seamless user experience.</p>

<h3 style="color: #2e6c80;">Technologies and Tools Used:</h3>
<table>
  <tbody>
    <tr>
      <td>Spring Boot</td>
      <td>The core framework for building the application, providing support for RESTful services and web infrastructure.</td>
    </tr>
    <tr>
      <td>Spring MVC</td>
      <td>A core part of the Spring framework, used to implement the Model-View-Controller design pattern. It separates the application into three interconnected components: Model (data), View (UI), and Controller (business logic), improving scalability and maintainability.</td>
    </tr>
    <tr>
      <td>Spring Security</td>
      <td>Used to secure the API, handling user authentication and authorization.</td>
    </tr>
    <tr>
      <td>Spring Data JPA</td>
      <td>Provides an abstraction layer for database operations with PostgreSQL, simplifying data access and manipulation.</td>
    </tr>
    <tr>
      <td>Spring Data REST</td>
      <td>Builds on Spring Data repositories to automatically expose REST endpoints based on the domain model. It simplifies development by reducing the need to write controllers for CRUD operations on entities.</td>
    </tr>
    <tr>
      <td>Spring Boot Testing</td>
      <td>Includes tools for testing the application's components, such as JUnit and Mockito. This ensures the reliability of the codebase by validating business logic, APIs, and database interactions in an automated way.</td>
    </tr>
    <tr>
      <td>Liquibase</td>
      <td>A tool to manage database schema changes and versioning in a consistent manner.</td>
    </tr>
    <tr>
      <td>JWT</td>
      <td>Used for user authentication and session management through JSON Web Tokens.</td>
    </tr>
    <tr>
      <td>Swagger/OpenAPI</td>
      <td>Generates interactive API documentation, making it easier to understand and consume the services.</td>
    </tr>
    <tr>
      <td>MapStruct</td>
      <td>A Java annotation processor that generates type-safe and performant mappers for Java bean classes. It simplifies the process of mapping data between DTOs (Data Transfer Objects) and entities, ensuring type safety and performance optimization.</td>
    </tr>
    <tr>
      <td>Stripe</td>
      <td>Integrates a secure payment gateway for processing accommodation booking payments.</td>
    </tr>
    <tr>
      <td>Telegram Bot</td>
      <td>Notifies users about the status of their bookings through Telegram messages.</td>
    </tr>
    <tr>
      <td>Postman</td>
      <td>Used to test API requests and visualize the responses for ease of development and debugging.</td>
    </tr>
    <tr>
      <td>Docker</td>
      <td>Containerization tool to run the project in isolated environments, ensuring consistency across different systems.</td>
    </tr>
  </tbody>
</table>

<p>Components of the application:</p>
<ol>
  <li><i>Model</i>: Represents the data and business logic. In this project, it includes entities like User, Accommodation, Booking, and Payment.</li>
  <li><i>View</i>: This component is responsible for displaying data in a user-friendly format. It could be part of the frontend, but in the case it's APIs returning data in JSON format.</li>
  <li><i>Controller</i>: Handles user input and interaction with the model, typically through RESTful API requests. In Spring MVC, this layer mediates between the frontend and backend.</li>
</ol>

<h3 style="color: #2e6c80;">UML Diagram of the system's models</h3>
<p>The UML diagram provides a visual representation of the relationships between the entities (models) in the system. It shows how entities like User, Accommodation, Booking, and Payment are related to each other and how they interact in the system. This helps in understanding the overall structure and flow of the system.</p>
<img src="/img/diagram-uml.png" alt="UML diagram" style="width:1400px;height:900px;">  


<p>Key entities (models) in this application:</p>
<ul>
    <li><u>AbstractEntity</u>: A base class for all entities in the application, providing common fields such as `id` (primary key) and `isDeleted` (soft delete marker: not removed but marked as inactive using the `@SQLDelete` and `@SQLRestriction` annotations). Entities like `Accommodation`, `Amenity`, `User`, and others extend this class to ensure consistency and reduce redundant code. The `@MappedSuperclass` annotation ensures that its fields are inherited by subclasses and mapped to their respective database tables.</li>
    <li><u>User</u>: Stores information about the registered user, including their authentication details.</li>
    <li><u>Role</u>: Represents the roles assigned to users for managing their access and permissions within the system. Each role is uniquely identified by its name (e.g., ADMIN, CUSTOMER).</li>
    <li><u>RoleName</u>: An enumeration defining the possible roles in the system. Each role (e.g., ADMIN, CUSTOMER) is associated with a string representation and can be retrieved programmatically.</li>
    <li><u>Status</u>: An enumeration representing the status of bookings and payments. Possible statuses include `PENDING`, `CONFIRMED`, `CANCELED`, `EXPIRED`, and `PAID`.</li>
    <li><u>Type</u>: An enumeration defining the types of accommodations available in the system. Examples include `APARTMENT`, `HOTEL`, `VILLA`, and `VACATION_HOME`.</li>
    <li><u>Amenity</u>: Represents the features or facilities available in an accommodation, such as Wi-Fi, parking, or a swimming pool. Amenities are associated with accommodations to help users filter and select properties based on their preferences.</li>
    <li><u>Location</u>: Represents the geographical location of an accommodation, including details such as the address, city, state, and country. The Location entity is essential for search and filtering operations based on user-specified areas.</li>
    <li><u>Accommodation</u>: Represents available properties such as apartments or houses.</li>
    <li><u>Booking</u>: Represents a booking made by a user for accommodation.</li>
    <li><u>Payment</u>: Handles the payment details for a booking.</li>
</ul>


<p>Example response from an API endpoint:</p>
<pre><code class="language-json">
[
  {
    "id": 1,
    "type": "VACATION_HOME",
    "location": {
        "id": 1,
        "country": "Ukraine",
        "city": "Novy Yar",
        "region": "Lviv region",
        "zipCode": "81050",
        "address": "Yavorian Lake, SIRKA SPORT",
        "description": "cell: +38(073)8761234, https://sirka.ua, Google Maps(49.951422, 23.488195)"
    },
    "size": "1 Bedroom",
    "amenities": [
        {
            "id": 3,
            "title": "Hairdryer",
            "description": null
        },
        {
            "id": 1,
            "title": "WiFi",
            "description": "WiFi is free"
        },
        {
            "id": 2,
            "title": "Parking(secured)",
            "description": "Private parking"
        }
    ],
    "dailyRate": 100.00,
    "availability": 2
}
]
</code></pre>

<h3 style="color: #2e6c80;">Controllers in the project:</h3>
<ul>
    <li><u>AuthenticationController</u>: Manages user authentication and registration:
        <ul>
            <li><strong>Registration</strong>: Creates a new user entity in the database.</li>
            <li><strong>Login</strong>: Authenticates an existing user and returns a token for further requests.</li>
        </ul>
    </li>
    <li><u>AccommodationController</u>: Manages accommodations and allows admins to perform CRUD operations:
        <ul>
            <li><strong>Create a new accommodation</strong>: Admins can create a new accommodation entry in the database.</li>
            <li><strong>Get all accommodations</strong>: Fetches a list of all accommodations with pagination and sorting.</li>
            <li><strong>Get accommodation by ID</strong>: Retrieves an accommodation entity based on its unique ID.</li>
            <li><strong>Update accommodation details</strong>: Admins can update specific details of an accommodation.</li>
            <li><strong>Update all accommodation information</strong>: Admins can update an accommodation’s complete details, including location and amenities.</li>
            <li><strong>Delete accommodation</strong>: Admins can mark an accommodation entity as deleted by ID (soft delete).</li>
        </ul>
    </li>
    <li><u>BookingController</u>: Manages bookings for users and admins:
        <ul>
            <li><strong>Create a new booking</strong>: Allows users to create a new accommodation booking.</li>
            <li><strong>Get all bookings by user and status</strong>: Retrieves bookings based on a specific user and status (for admins).</li>
            <li><strong>Get all bookings for the logged-in user</strong>: Allows users to see their own bookings.</li>
            <li><strong>Get a specific booking by ID for the logged-in user</strong>: Provides details of a specific booking for the user who owns it.</li>
            <li><strong>Get a specific booking by ID for admin</strong>: Provides full booking details for an admin.</li>
            <li><strong>Update a booking's status</strong>: Allows admins to update the status of a booking.</li>
            <li><strong>Cancel a booking</strong>: Lets users cancel their booking by marking it as canceled.</li>
        </ul>
    </li>
    <li><u>PaymentController</u>: Handles payments for booking transactions:
        <ul>
            <li><strong>Create payment session</strong>: Initiates payment sessions for booking transactions that are unpaid.</li>
            <li><strong>Pay a booking by sessionId</strong>: Handles successful payment processing through Stripe redirection.</li>
            <li><strong>Cancel payment by sessionId</strong>: Informs about the cancellation of a payment session.</li>
            <li><strong>Get all payments by userId</strong>: Admins can retrieve all payment information for a specific user.</li>
            <li><strong>Get all payments for owner</strong>: Retrieves payment information for the logged-in user.</li>
        </ul>
    </li>
    <li><u>UserController</u>: Manages user-related actions such as retrieving and updating user details:
        <ul>
            <li><strong>Get current user's details</strong>: Retrieves the profile information for the currently logged-in user.</li>
            <li><strong>Update user's role</strong>: Allows admins to update the role of a user (e.g., from CUSTOMER to ADMIN).</li>
            <li><strong>Update profile</strong>: Allows users to update their own profile information.</li>
        </ul>
    </li>
</ul>

<p>API Documentation is accessible via Swagger UI. To view it after successful authentication, navigate to: http://localhost:8080/api/swagger-ui/index.html</p>
<img src="/img/swagger_1.png" alt="All endpoints - part 1" style="width:704px;height:1834px;">  
<img src="/img/swagger_2.png" alt="All endpoints - part 2" style="width:903px;height:756px;">

<p>To run the project with Docker, use the following commands:</p>
<pre><code>
docker-compose build
docker-compose up
</code></pre>

<p>For running with updated images:</p>
<pre><code>
docker compose up --build
</code></pre>

<p>To stop and remove the containers when done:</p>
<pre><code>
docker-compose down
</code></pre>

<p>This will start two containers: one for the application and one for the PostgreSQL database. API requests can be made on port 8081 (http://localhost:8081/api...).</p>
