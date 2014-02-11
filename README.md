Scanalyzer
==========

A ReSTful service application which will track and scan your network hosts using NMAP and store the results. (COMPLETED)

It will also compare previous results with the current results. (COMPLETED)

It will generate warning messages about changes to your infrastructure. (COMPLETED)

## Prerequisites

This application assumes a *NIX style filesystem. It also requires the console [NMAP](http://nmap.org/) to be installed.
It also needs a mail server to send alerts through. Finally, if you want/need to use something other than the
embedded HSQLDB database, add the dependency to the Maven POM file and update the settings in your configuration file.

By default, the embedded HSQLDB wants to create it's files in /var/lib/scanalyzer and it will need read and write permissions
to create the database files.

## Building

To build the application, make sure that you have [Maven](http://maven.apache.org/) >= 3.x and run:

```
mvn clean package
```

## Running

To run the application after building it, execute "java -jar target/scanalyzer-<version>.jar"

Possible arguments are:

```
scanalyzer [-c <config file>] [-b <bind address>] [-p <bind port>] [-t <thread count>] [---mailuser <username>] [--mailpass <password>]
	--config=<config file> || -c <config file>         The path to a configuration file (Default: Checks for /etc/scanalyzer/scanalyzer.conf).
	--bind=<bind address> || -b <bind address>         The address on which to listen on for web clients (defaults to 127.0.0.1).
	--port=<bind port> || -p <bind port>               The port on which to listen on for web clients (defaults to 8080).
	--threads=<thread count> || -t <thread count>      The number of concurrent scanning threads which can run (defaults to 5).
	--mailuser=<username> || -u <username>             A username with which to authenticate for sending e-mail.
	--mailpass=<password> || -w <password>             A password with which to authenticate for sending e-mail.
	--help || -h                                       Shows this help text.                                      Shows this help text.
```

For an example of ALL of the config file options, look at [scanalyzer.properties](src/main/resources/scanalyzer.properties)


Once the application is running, you can access the Swagger API interface at:

```
http://<host address>:<port>/static/
```
