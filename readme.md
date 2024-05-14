

<br>
<h1 align="center">Spark Middleware Data Scrapper</h1>
<br>

<p align="center">
  This application is designed to scrape market data from the Binance API and retrieve it using REST or WebSocket clients. Scraped data is then visualized on a responsive, user-friendly, and colorful chart powered by Chart.js.
</p>

<p align="center">
  <img src="readme/chart-overview.png" style="border-radius: 20px;">
</p>
<br>

<h1 align="center">Data Scraping</h1>

<h1 align="left">
Source:
</h1>
Application scrapes market data from Binance, including prices, trading volumes, and other relevant metrics.

<h1 align="left">
Frequency: 
</h1>
Data can be scraped at regular intervals or in real-time, depending on the configuration.

<h1 align="left">
Asychronously: 
</h1>
Scraping the Binance API and pushing updates is performed asynchronously to ensure optimal real-time data retrieval for all currencies simultaneously.
This approach maximizes efficiency, allowing the data service and WebSocket sessions to be updated as quickly as possible, ensuring users receive the most current information without delay.
<br>

<h1 align="left">
Connection failure: 
</h1>
Scrapper service is designed to handle connection timeouts or failures gracefully.
It will automatically retry each scrape or data push operation, with configurable retry attempts and intervals,
ensuring robust and reliable data collection even in the face of connectivity issues.
<br>


<h1 align="center">Data Service</h1>

<h1 align="left">
Rest API:
</h1>
Data service provides the capability to retrieve data from the database via REST API endpoints, allowing for flexible and on-demand data access.

<h1 align="left">
WebSocket: 
</h1>
Application also supports real-time data retrieval through WebSocket connections, enabling instantaneous updates and live data streaming directly from the datasource.

<h1 align="left">
Time Zone Support : 
</h1>
Data service supports user-specified time zones, ensuring that data values are returned in the correct local time for the specified region.



<br>
<br>
<br>


<h1 align="center">Fully scaled & tracked infrastructure (almost)*</h1>

<p align="center">
Application is build as a fully scaled infrastructure with possibility to load up more instances if needed.
</p>
<br>
<p align="center">
*
Websocket service needs to be upgraded with external DB storage for session data storage.I recommend mongoDB. Then we need message broker for dynamic user connected instanceId recognition.
</p>


<h2 align="left">
Eureka: 
</h2>

Used for service discovery and registration, allowing services to locate and communicate with each other without hardcoding service locations.

<h2 align="left">
Spring Cloud Feign: 
</h2>

Simplifies the development of RESTful clients by providing a higher-level abstraction over HTTP clients. This enables declarative REST calls between microservices.

<h2 align="left">
Prometheus: 
</h2>

An open-source monitoring and alerting toolkit that collects metrics from your services and infrastructure, helping you detect and diagnose issues in real time.

<h2 align="left">
WebSocket Service: 
</h2>

Utilizes SockJS and STOMP protocol for handling real-time, session-based communication.

<h2 align="left">
Grafana with Loki and Promtail:
</h2>
Grafana is used for metric visualization and monitoring dashboards, with Loki log aggregation system and Promtail that ships the logs to Loki are dispatched for logging and log aggregation.


<br>
<br>
<br>




<h1 align="center">Project compilation & local docker mount:</h1>

```bash
 mvn clean install jib:dockerBuild -Plocal
```

<h1 align="center">Docker deployment:</h1>

```bash
docker compose up
```
<br>


<h1 align="center">Running on localhost environment</h1>

```bash
 mvn clean install
```

<h2 align="center">& run all spring services</h2>

<p align="center">
  <img src="readme/local-environment-run.png" style="border-radius: 10px;">
</p>

<br>
<br>


<h2 align="center">
  After starting the application, please wait about one minute until all clients register correctly with the Eureka server.
</h2>

<br>
<br>


<h1 align="center">Services Url's:</h1>

|       Service        |                       LOCAL                        |                                          OpenAPI                                           | Login  |  Password   |
|:--------------------:|:--------------------------------------------------:|:------------------------------------------------------------------------------------------:|:------:|:-----------:|
|   Scrapper-Service   |  [http://localhost:9000/](http://localhost:9000/)  |                                             x                                              |   x    |      x      |
|     Data-Service     |  [http://localhost:9001/](http://localhost:9001/)  | [http://localhost:9001/swagger-ui/index.html](http://localhost:9001/swagger-ui/index.html) |   x    |      x      |
|  WebSocket-Service   |  [http://localhost:9002/](http://localhost:9002/)  |                                             x                                              |   x    |      x      |
|   Frontend-service   |  [http://localhost:4200/](http://localhost:4200/)  |                                             x                                              |   x    |      x      |
|     Api-Gateway      |  [http://localhost:9999/](http://localhost:9999/)  |                                             x                                              |   x    |      x      |
|    Eureka-Server     |  [http://localhost:8761/](http://localhost:8761/)  |                                             x                                              |   x    |      x      |
|                      |                                                    |                                                                                            |        |             |
|      Prometheus      |  [http://localhost:9090/](http://localhost:9090/)  |                                             x                                              |   x    |      x      |
|       Grafana        |  [http://localhost:3000/](http://localhost:3000/)  |                                             x                                              | admin  |   secret    |
|        Zipkin        |  [http://localhost:9411/](http://localhost:9411/)  |                                             x                                              |   x    |      x      |



<h1 align="center">Frontend Service API:</h1>

|                             SERVICE URL                              |                             GATEWAY URL                              |                  Description                  |
|:--------------------------------------------------------------------:|:--------------------------------------------------------------------:|:---------------------------------------------:|
| [http://localhost:4200/chart-rest](http://localhost:4200/chart-rest) | [http://localhost:9999/chart-rest](http://localhost:9999/chart-rest) |    Data visualisation based on REST client    |
|   [http://localhost:4200/chart-ws](http://localhost:4200/chart-ws)   |   [http://localhost:9999/chart-ws](http://localhost:9999/chart-ws)   | Data visualisation based on WebSocket client  |

<h1 align="center">Data Service API:</h1>

| METHOD |                                                         URL                                                         |   REQUEST PARAMS    |  PATH VARIABLE  |            REQUEST BODY             |                     Description                      |
|:------:|:-------------------------------------------------------------------------------------------------------------------:|:-------------------:|:---------------:|:-----------------------------------:|:----------------------------------------------------:|
|  GET   |                            [/api/v1/currencies](http://localhost:9001/api/v1/currencies)                            | String {userZoneId} |        x        |                  x                  |         Returns all available currency pairs         |
|  GET   |           [/api/v1/currencies/lastDay/{symbol}](http://localhost:9001/api/v1/currencies/lastDay/BTCUSDT)            | String {userZoneId} | String {symbol} |                  x                  | Returns last 24h chart history of specified currency |
|  GET   |                    [/api/v1/currencies/lastAll](http://localhost:9001/api/v1/currencies/lastAll)                    | String {userZoneId} |        x        |                  x                  |  Returns all available currency pairs latest values  |
|  GET   |              [/api/v1/currencies/last/{symbol}](http://localhost:9001/api/v1/currencies/last/BTCUSDT)               | String {userZoneId} | String {symbol} |                  x                  |        Returns last specified currency value         |
|        |                                                                                                                     |                     |                 |                                     |                                                      |
|  PUT   |                 [/api/v1/management/currencies](http://localhost:9001/api/v1/management/currencies)                 |          x          |        x        |   CurrencyPairUpdateRequest.Class   |   Updates currency pair data based on request data   |
|  POST  |                 [/api/v1/management/currencies](http://localhost:9001/api/v1/management/currencies)                 |          x          |        x        |   CurrencyPairCreateRequest.Class   |   Creates currency pair data based on request data   |
| DELETE |                 [/api/v1/management/currencies](http://localhost:9001/api/v1/management/currencies)                 |          x          |        x        |   CurrencyPairDeleteRequest.Class   |   Deletes currency pair data based on request data   |
|  POST  | [/api/v1/management/currencies/scrapper/update](http://localhost:9001/api/v1/management/currencies/scrapper/update) |          x          |        x        | ScrappedCurrencyUpdateRequest.Class | Internal method for scrapper service to update data  |

<h1 align="center">WebSocket Service API:</h1>

| METHOD |                                                           URL                                                           |  REQUEST PARAMS   | DESTINATION VARIABLE |            REQUEST BODY             |                     Description                      |
|:------:|:-----------------------------------------------------------------------------------------------------------------------:|:-----------------:|:--------------------:|:-----------------------------------:|:----------------------------------------------------:|
|   x    | [http://localhost:9002/websocket/info?timezone={timeZone}](http://localhost:9002/websocket/info?timezone=Europe/Warsaw) | String {timezone} |          x           |                  x                  |                  HandShake endpoint                  |
|   x    |                    [ws://localhost:9002/ws/api/v1/currencies](ws://localhost:9002/api/v1/currencies)                    |         x         |          x           |                  x                  |         Returns all available currency pairs         |
|   x    |  [ws://localhost:9002/ws/api/v1/currencies/lastDay/{symbol}](http://localhost:9002/api/v1/currencies/lastDay/BTCUSDT)   |         x         |   String {symbol}    |                  x                  | Returns last 24h chart history of specified currency |
|   x    |        [ws://localhost:9002/ws/api/v1/currencies/last/lastAll](http://localhost:9002/api/v1/currencies/lastAll)         |         x         |          x           |                  x                  |  Returns all available currency pairs latest values  |
|   x    |     [ws://localhost:9002/ws/api/v1/currencies/last/{symbol}](ws://localhost:9002/ws/api/v1/currencies/last/BTCUSDT)     |         x         |   String {symbol}    |                  x                  |        Returns last specified currency value         |
|        |                                                                                                                         |                   |                      |                                     |                                                      |                                     |                                                      |
|  PUT   |                       [http://localhost:9002/api/v1/events](http://localhost:9002/api/v1/events)                        |         x         |          x           | ScrappedCurrencyUpdateRequest.Class | Internal method for scrapper service to update data  |