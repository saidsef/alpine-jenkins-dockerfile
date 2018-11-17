#!groovy
// https://mrooding.me/dockerized-jenkins-2-on-google-cloud-platform-34747725e786
// https://go.cloudbees.com/docs/cloudbees-documentation/admin-instance/setup/

import jenkins.model.*
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import hudson.extension.*

def instance = Jenkins.getInstance()
def password = System.getenv("JENKINS_ADMIN_PASSWORD") ?: UUID.randomUUID().toString()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount('admin', password)
instance.setSecurityRealm(hudsonRealm)
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
instance.save()

instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
instance.save()

// Updated Theme
def ipAddress = InetAddress.localHost.hostAddress
def colours = ['blue','green','yellow','cyan','lime','blue-grey']
def file = new File("./colour")
def r = new Random()

for (pd in PageDecorator.all()) {
  def colour = (file.exists()) ? file.getText() : colours.get(r.nextInt(colours.size()))
  file.write colour
  if (pd instanceof org.codefirst.SimpleThemeDecorator) {
    println "--> updating jenkins theme - ${colour}"
    pd.setCssUrl("https://cdn.rawgit.com/afonsof/jenkins-material-theme/gh-pages/dist/material-${colour}.css")
  }
  pd.load()
}

println "#########################################################"
println "--> created local user 'admin' with password: ${password}"
println "#########################################################"
