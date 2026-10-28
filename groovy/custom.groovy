#!groovy
// https://mrooding.me/dockerized-jenkins-2-on-google-cloud-platform-34747725e786
// https://go.cloudbees.com/docs/cloudbees-documentation/admin-instance/setup/
// move to new format: https://plugins.jenkins.io/role-strategy/

import hudson.extension.*
import hudson.model.*
import hudson.plugins.audit_trail.AuditTrailPlugin
import hudson.plugins.audit_trail.ConsoleAuditLogger
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import io.jenkins.plugins.opentelemetry.JenkinsOpenTelemetryPluginConfiguration
import jenkins.model.*
import jenkins.security.csp.impl.CspConfiguration
import org.jenkinsci.plugins.matrixauth.AuthorizationType
import org.jenkinsci.plugins.matrixauth.PermissionEntry

def instance = Jenkins.getInstance()
def envPassword = System.getenv("JENKINS_ADMIN_PASSWORD")
def generated   = !envPassword?.trim()
def password    = generated ? UUID.randomUUID().toString() : envPassword
def host        = System.getenv("JENKINS_HOSTNAME") ?: InetAddress.localHost.hostAddress.toString()

// email parameters
def jenkinsParameters = [
  email:  "Mr Jenkins <jenkins@${host}>",
  url:    "https://${host}/"
]

try {
  // set Jenkins Admin URL and email
  JenkinsLocationConfiguration jlc = JenkinsLocationConfiguration.get()
  jlc.setUrl(jenkinsParameters.url)
  jlc.setAdminAddress(jenkinsParameters.email)
  jlc.save()
} catch (Exception e) {
  println "--> Jenkins Admin URL and email failed"
  println "${e}"
}

try {
  // set Hudson Private Security Realm
  def hudsonRealm = new HudsonPrivateSecurityRealm(false)
  hudsonRealm.createAccount('admin', password)
  hudsonRealm.createAccount('saidsef', password)
  instance.setSecurityRealm(hudsonRealm)
  instance.setNumExecutors(1)
  instance.setSlaveAgentPort(-1)
  instance.save()
} catch (Exception e) {
  println "--> Hudson Private Security Realm failed"
  println "${e}"
}

try {
  // set Full Control Once Logged In Auth
  def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
  strategy.setAllowAnonymousRead(false)
  instance.setAuthorizationStrategy(strategy)
  instance.save()
} catch (Exception e) {
  println "--> Full Control Once Logged In Auth failed"
  println "${e}"
}

try {
  // set Global Matrix Auth
  def matrix = new GlobalMatrixAuthorizationStrategy()
  matrix.add(Jenkins.ADMINISTER, new PermissionEntry(AuthorizationType.USER, 'admin'))
  // info found from http://javadoc.jenkins-ci.org/hudson/security/class-use/Permission.html#jenkins.slaves
  matrix.add(Jenkins.ADMINISTER, new PermissionEntry(AuthorizationType.USER, 'saidsef'))
  instance.setAuthorizationStrategy(matrix)
  instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
  instance.save()
  instance.reload()
} catch (Exception e) {
  println "--> Global Matrix Auth failed"
  println "${e}"
}

try {
  // Audit Trail records nothing without a logger, and stdout goes to the container log
  AuditTrailPlugin audit = instance.getDescriptorByType(AuditTrailPlugin.class)
  if (!audit.getLoggers().any { it instanceof ConsoleAuditLogger }) {
    audit.setLoggers(audit.getLoggers() + new ConsoleAuditLogger(ConsoleAuditLogger.Output.STD_OUT, "yyyy-MM-dd HH:mm:ss", "audit"))
    audit.save()
  }
  println "--> audit trail logging to the console"
} catch (Exception e) {
  println "--> audit trail console logger failed"
  println "${e}"
}

try {
  // The plugin leaves the SDK as a no-op unless its own config names an endpoint,
  // so OTEL_EXPORTER_OTLP_ENDPOINT on its own does not export anything
  def otlpEndpoint = System.getenv("OTEL_EXPORTER_OTLP_ENDPOINT")
  if (otlpEndpoint?.trim()) {
    JenkinsOpenTelemetryPluginConfiguration otel = JenkinsOpenTelemetryPluginConfiguration.get()
    otel.setEndpoint(otlpEndpoint)
    otel.configureOpenTelemetrySdk()
    otel.save()
    println "--> opentelemetry exporting to ${otlpEndpoint}"
  }
} catch (Exception e) {
  println "--> opentelemetry endpoint failed"
  println "${e}"
}

try {
  // Jenkins only reports policy violations until enforcement is on, and it does not block anything
  GlobalConfiguration.all().get(CspConfiguration).setEnforce(true)
  println "--> content security policy enforced"
} catch (Exception e) {
  println "--> content security policy enforcement failed"
  println "${e}"
}

println "#########################################################"
if (generated) {
  println "--> created local users 'admin' and 'saidsef' with generated password: ${password}"
  println "--> generated because JENKINS_ADMIN_PASSWORD was unset - rotate it after first login"
} else {
  println "--> created local users 'admin' and 'saidsef' with the password from JENKINS_ADMIN_PASSWORD"
}
println "--> IP address ${jenkinsParameters.url}"
println "#########################################################"
