# Microservices Arquitecture
In this repository we will find multiple proyects from a microservices arquitecture which i considere them to be among bases.

### Eureka Server
A server which will tell us where our microservice are allocated so we dont need to know to reference them

### Config Server
Changing configuration on an application can be sometimes tricky due to restar being necessary. Using an externalize configuration server can be usefull to update configuration on runtime without restarting apps

### Gateway Server
Our gateway server so all the request made to our microservices has to be routed by this server and to apply some authorization and authentications filters.

### Auth Server
Server used not only to register users but also to authorize them (JWT Token) and validate the tokens needed to make requests.

### Client and Library microservices
Finaly our application microservices. In this case, client microservice is used to call library microservice using Feign Client and differents load balancing configurations.


