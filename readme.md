# Automate CSB (SERVER) I am adding stuff

### Project Setup
- You will need <a href="https://www.jetbrains.com/idea/">Intellij Ultimate edition</a> as your IDE
    - It is available for free for students if you sign up with your Lehigh email
- This project is best used by importing it via SBT
- This project is based on the Play! Framework
    - More information on play 2.6.x can be found <a href="https://www.playframework.com/documentation/2.6.x/Home">here</a> 
- You will need to learn how to use eBean ORM to interact with the database.
    - More information on eBean can be found <a href="http://ebean-orm.github.io/">here</a>

### Compiling
- To compile this project, you need to install the Scala plugin for intellij
    - You can get the plugin by going to settings/preferences --> plugins --> browse repositories --> scala
    - Be sure to restart Intellij afterward
- Go to the <i>SBT shell</i> in Intellij Idea
- Once it's initialized, run the <i>compile</i> command
- To compile test code, run the <i>test:compile</i> command

### Design
- This project uses MVC, dependency injection, and annotation processing a lot
    - If you're unfamiliar with these design patterns, checkout the following resources
    - <a href="https://stackoverflow.com/questions/130794/what-is-dependency-injection">Dependency Injection</a>
    - <a href="https://softwareengineering.stackexchange.com/questions/127624/what-is-mvc-really">MVC</a> 
- Dependency injection is used to separate implementations between a testing and production environment
- For an example of this, check out the usage of <i>InMemoryCache</i> vs. <i>CacheWrapper</i> in the <i>cache</i> package.
- A good example of annotation processing is our usage of Swagger UI
    - Swagger is an "automagical" documentation creator that generates our RESTful API docs
    - By annotating our routes, we can automatically create docs for consumers of the API to read and use for integration
    - For more information on Swagger, see <a href="https://github.com/swagger-api/swagger-play/tree/master/play-2.6/swagger-play2">here</a>

### Testing
- Use the dummy-data-creation.sql and dummy-data-destruction.sql files to run tests.
    - Ideally, this data is inputted into an in-memory database which allows for a clean slate to be used for each test
    - The <i>BaseTestApplicationForTesting</i> covers this design already, but it's good to be aware of.
    - The <i>BaseTestApplicationForProduction</i> is used to run tests against the live database. Use this with caution!
- You can setup tests via Intellij
    - Goto run --> edit configurations --> + --> junit
    - Get rid of the <i>build</i> task from "before launch" configurations
    - To properly compile before running tests, run <i>sbt test:compile</i>
    - Then, run tests via the "run" command in intellij
    
### Deploying to Heroku
- Push to the <i>master</i> branch to deploy to the production site
    - This maps to <a href="https://automate-csb.herokuapp.com">automate-csb.herokuapp.com</a>
- Push to the <i>debug</i> branch to deploy to the staging site
    - This maps to <a href="https://automate-csb-staging.herokuapp.com">automate-csb-staging.herokuapp.com</a> 
