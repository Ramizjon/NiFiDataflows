### DataDogReportingTask
Custom NiFi module that makes possible sending reporting info about processors activities to DataDog service using Gauges.

In order to use this module, you have to perform following actions:
- Make sure DataDog agent is installed
- Clone DataDogReportingTask submodule
- Build it with `mvn clean package`
- Put .NAR file from `nifi-datadog-nar/target` to NiFi installation directory `lib` folder
- Execute command `bin/nifi.sh stop` and then `bin/nifi.sh start`
- Add and launch DataDogReportingTask from reporting tasks menu
- Metrics should appear in DataDog dashboard

Adding DataDogReportingTask to NiFi dataflow
![Screenshot 4](https://s32.postimg.org/5e25zuaqd/Screenshot_at_Jul_11_17_50_03.png)

Metrics visualization in DataDog dashboard
![Screenshot 5](https://s32.postimg.org/j90gib55h/Screenshot_at_Jul_11_17_47_57.png)
