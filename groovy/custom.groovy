#!groovy
// https://mrooding.me/dockerized-jenkins-2-on-google-cloud-platform-34747725e786
// https://go.cloudbees.com/docs/cloudbees-documentation/admin-instance/setup/

import jenkins.model.*
import hudson.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import hudson.security.csrf.DefaultCrumbIssuer
import hudson.extension.*

Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

def instance = Jenkins.getInstance()
def password = System.getenv("JENKINS_ADMIN_PASSWORD") ?: UUID.randomUUID().toString()
def host     = System.getenv("JENKINS_HOSTNAME") ?: InetAddress.localHost.hostAddress.toString()
def port     = System.getenv("JENKINS_PORT") ?: "8080".toString()

// email parameters
def jenkinsParameters = [
  email:  "Mr Jenkins <jenkins@${host}>",
  url:    "http://${host}:${port}/"
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
// instance.getDescriptor("jenkins.CLI").get().setEnabled(false)
instance.setSlaveAgentPort(-1);
instance.save()

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)
instance.save()

def matrix = new GlobalMatrixAuthorizationStrategy()
matrix.add(Jenkins.ADMINISTER, "admin")
matrix.add(Jenkins.READ,'authenticated')
matrix.add(Item.READ,'authenticated')
matrix.add(Item.DISCOVER,'authenticated')
matrix.add(Item.CANCEL,'authenticated')
matrix.add(Item.READ,'anonymous')
// info found from http://javadoc.jenkins-ci.org/hudson/security/class-use/Permission.html#jenkins.slaves
matrix.add(Jenkins.ADMINISTER, "saidsef")
instance.setAuthorizationStrategy(matrix)
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
instance.save()

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
