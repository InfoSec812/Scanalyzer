Scanalyzer
==========

A ReSTful service application which will track and scan your network hosts using NMAP and store the results. It will also compare previous results with the current results and potentially generate warning messages about changes to your infrastructure.

To build the application, make sure that you have Maven >=3.x and run:

```
mvn clean package
```

To run the application after building it, execute "java -jar target/scanalyzer-<version>.jar"

Possible arguments are:

```
scanalyzer [-c <config file>] [-b <bind address>] [-p <bind port>] [-t <thread count>]
	--config=<config file> || -c <config file>         The path to a configuration file (Default: Checks for /etc/scanalyzer/scanalyzer.conf).
	--bind=<bind address> || -b <bind address>         The address on which to listen on for web clients (defaults to 127.0.0.1).
	--port=<bind port> || -p <bind port>               The port on which to listen on for web clients (defaults to 8080).
	--threads=<thread count> || -t <thread count>      The number of concurrent scanning threads which can run (defaults to 5).
	--help || -h                                       Shows this help text.
```

For an example of ALL of the config file options, look at [scanalyzer.properties](src/main/resources/scanalyzer.properties)


Once the application is running, add a host using cURL like this:

```
curl -H "Accept: application/xml" -H "Content-Type: application/xml" -X POST "http://<host>:<port>/rest/host/address/192.168.1.1"
```

After a few seconds the first scan will have run and you can view all of a host's scans with the following request:

```
curl -H "Accept: application/xml" -X GET "http://<host>:<port>/rest/scan/host/{host id}"
```
