import groovy.json.JsonSlurper;

// Create a map of environment and namespace associated
def envNsMap = [
  "foo1": ["dev", "test"],
  "bar1": ["dev", "test"],
  "foo2": ["preprod", "prod"],
  "bar2": ["preprod", "prod"],
  "blue-sandbox": ["dev", "test"],
  "green-sandbox": ["dev", "test"]
]

def getPriURL() {
  try{
    List<String> repoList = new ArrayList<String>()
    
    URL apiUrl1 = "foo1".toURL()
    List json1 = new JsonSlurper().parse(apiUrl1.newReader())
    for (repo in json1) { 
      repoList.add('pri/'+repo.name) 
    }

    URL apiUrl2 = "bar1".toURL()
    List json2 = new JsonSlurper().parse(apiUrl2.newReader())
    for (repo in json2) { 
      repoList.add('ClientPortal/'+repo.name) 
    }
    
    URL apiUrl3 = "foo2".toURL()
    List json3 = new JsonSlurper().parse(apiUrl3.newReader())
    for (repo in json3) { 
      repoList.add('NextGen/'+repo.name) 
    }
    
    URL apiUrl4 = "bar2".toURL()
    List json4 = new JsonSlurper().parse(apiUrl4.newReader())
    for (repo in json4) { 
      repoList.add('Reins/'+repo.name) 
    }
    return repoList
  }
  catch(IOException ex){
    print ex
  }
}

// Create Application Folders and MultiBranch

folder("Application_Deployments")

for (env in envNsMap) {
  folder("Application_Deployments/${env.key}")
  
  for (ns in envNsMap[env.key]) {
    folder("Application_Deployments/${env.key}/${ns}")
    folder("Application_Deployments/${env.key}/${ns}/a")
    folder("Application_Deployments/${env.key}/${ns}/b")
    folder("Application_Deployments/${env.key}/${ns}/c")
    folder("Application_Deployments/${env.key}/${ns}/d")
    
    for (repo in getPriURL()) {
      multibranchPipelineJob("Application_Deployments/${env.key}/${ns}/${repo}") {
        branchSources {
          branchSource {
            source {
              git {
                id(repo)
                remote('https://github.com/'+repo+'.git')
                credentialsId('abc-foo-bar-id')
                traits {
                  headWildcardFilter {
                    includes('*')
                    excludes(null)
                  }
                }
              }
              strategy {
                defaultBranchPropertyStrategy {
                  props {}
                }
              }
            }
          }
        }
        orphanedItemStrategy {
          discardOldItems {
            numToKeep(5)
          }
        }
        configure {
          def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
          traits << 'jenkins.plugins.git.traits.BranchDiscoveryTrait' {}
        }
      }
    }
  }
}
