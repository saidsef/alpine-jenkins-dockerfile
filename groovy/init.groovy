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

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

def matrix = new GlobalMatrixAuthorizationStrategy()
matrix.add(Jenkins.ADMINISTER, "admin")
instance.setAuthorizationStrategy(strategy)

instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
instance.setSlaveAgentPort(9099)

instance.save()

// Updated Theme
def ipAddress = InetAddress.localHost.hostAddress
def r = new Random()
def colours = ['blue','green','amber','orange']

for (pd in PageDecorator.all()) {
  if (pd instanceof org.codefirst.SimpleThemeDecorator) {
    def colour = colours.get(r.nextInt(colours.size()))
    println "--> updating jenkins theme - ${colour}"
    pd.cssUrl = "https://cdn.rawgit.com/afonsof/jenkins-material-theme/gh-pages/dist/material-${colour}.css"
  }
}
