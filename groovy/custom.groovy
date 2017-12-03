#!groovy
// https://mrooding.me/dockerized-jenkins-2-on-google-cloud-platform-34747725e786
// https://go.cloudbees.com/docs/cloudbees-documentation/admin-instance/setup/

import jenkins.model.*
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import hudson.extension.*

def instance = Jenkins.getInstance()

println "--> creating local user 'admin'"

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount('admin','admin')
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
matrix.add(Item.CONFIGURE,'jenkins-job-builder')
matrix.add(Item.READ,'jenkins-job-builder')
matrix.add(Item.READ,'anonymous')
matrix.add(Item.DISCOVER,'jenkins-job-builder')
matrix.add(Item.CREATE,'jenkins-job-builder')
matrix.add(Item.DELETE,'jenkins-job-builder')
matrix.add(Jenkins.ADMINISTER, "swarm-slave")
matrix.add(Jenkins.ADMINISTER, "jenkins-job-builder")
// declare who can launch a slave (using awarm client) and configure the slaves
// add slave launch permissions to svc
// info found from http://javadoc.jenkins-ci.org/hudson/security/class-use/Permission.html#jenkins.slaves
matrix.add(Computer.BUILD,'swarm-slave')
matrix.add(Computer.CONFIGURE,'swarm-slave')
matrix.add(Computer.CONNECT,'swarm-slave')
matrix.add(Computer.CREATE,'swarm-slave')
matrix.add(Computer.DISCONNECT,'swarm-slave')
instance.setAuthorizationStrategy(matrix)
instance.save()

instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
instance.save()

// Updated Theme
def ipAddress = InetAddress.localHost.hostAddress
def r = new Random()
def colours = ['blue','green','amber','orange','red','yellow']

for (pd in PageDecorator.all()) {
  if (pd instanceof org.codefirst.SimpleThemeDecorator) {
    def colour = colours.get(r.nextInt(colours.size()))
    println "--> updating jenkins theme - ${colour}"
    pd.cssUrl = "https://cdn.rawgit.com/afonsof/jenkins-material-theme/gh-pages/dist/material-${colour}.css"
  }
}
