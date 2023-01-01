#!groovy
// https://mrooding.me/dockerized-jenkins-2-on-google-cloud-platform-34747725e786
// https://go.cloudbees.com/docs/cloudbees-documentation/admin-instance/setup/
// move to new format: https://plugins.jenkins.io/role-strategy/

import hudson.extension.*
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.*
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

// set Jenkins Admin URL and email
def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()
jenkinsLocationConfiguration.setUrl(jenkinsParameters.url)
jenkinsLocationConfiguration.setAdminAddress(jenkinsParameters.email)
jenkinsLocationConfiguration.save()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount('admin', password)
hudsonRealm.createAccount('saidsef', password)
instance.setSecurityRealm(hudsonRealm)
instance.setNumExecutors(1)
instance.setSlaveAgentPort(-1);
instance.save()

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)
instance.save()

def matrix = new GlobalMatrixAuthorizationStrategy()
matrix.add(Jenkins.ADMINISTER, new PermissionEntry(AuthorizationType.USER, 'admin'))
// info found from http://javadoc.jenkins-ci.org/hudson/security/class-use/Permission.html#jenkins.slaves
matrix.add(Jenkins.ADMINISTER, new PermissionEntry(AuthorizationType.USER, 'saidsef'))
instance.setAuthorizationStrategy(matrix)
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
instance.save()
instance.reload()

try {
  // Updated Theme
  def r = new Random()
  def ipAddress = InetAddress.localHost.hostAddress
  def colours = ['neo-light']
  def colour = colours.get(r.nextInt(colours.size()))

  def theme    = instance.getDescriptorByType(org.codefirst.SimpleThemeDecorator.class)
  theme.setElements([new org.jenkinsci.plugins.simpletheme.CssUrlThemeElement("https://tobix.github.io/jenkins-neo2-theme/dist/${colour}.css")])
  instance.save()

  println "--> updating jenkins theme to: ${colour}"
} catch(Exception e) {
  println "--> styling failed"
  println "${e}"
}

println "#########################################################"
println "--> created local user 'admin' with password: ${password}"
println "#########################################################"
