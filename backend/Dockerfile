FROM quay.io/wildfly/wildfly:29.0.1.Final-jdk17

# Copy the WAR file to the deployments directory
COPY target/securaccess-enterprise.war /opt/jboss/wildfly/standalone/deployments/

# Expose the default WildFly port
EXPOSE 8080

# Set the default command to run WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]