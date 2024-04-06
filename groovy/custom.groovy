#!groovy
// https://mrooding.me/dockerized-jenkins-2-on-google-cloud-platform-34747725e786
// https://go.cloudbees.com/docs/cloudbees-documentation/admin-instance/setup/
// move to new format: https://plugins.jenkins.io/role-strategy/

import hudson.extension.*
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.*
import java.security.SecureRandom
import org.jenkinsci.plugins.matrixauth.AuthorizationType
import org.jenkinsci.plugins.matrixauth.PermissionEntry

def instance = Jenkins.getInstance()
def password = System.getenv("JENKINS_ADMIN_PASSWORD") ?: UUID.randomUUID().toString()
def host     = System.getenv("JENKINS_HOSTNAME") ?: InetAddress.localHost.hostAddress.toString()

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
  // Update Theme and Style Sheet
  SecureRandom r = new SecureRandom()
  List<String> colours = ['green', 'teal', 'blue', 'grey', 'blue-grey']
  String colour = colours.get(r.nextInt(colours.size()))

  org.codefirst.SimpleThemeDecorator theme = instance.getDescriptorByType(org.codefirst.SimpleThemeDecorator.class)
  String url = "https://cdn.rawgit.com/afonsof/jenkins-material-theme/gh-pages/dist/material-${colour}.css"
  theme.setElements([new org.jenkinsci.plugins.simpletheme.CssUrlThemeElement(url)])

  println "--> updating jenkins theme to: ${colour}"
} catch (Exception e) {
  println "--> styling failed"
  println "${e}"
}

println "#########################################################"
println "--> created local user 'admin' with password: ${password}"
println "--> IP address ${jenkinsParameters.url}"
println "#########################################################"
