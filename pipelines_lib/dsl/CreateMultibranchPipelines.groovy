import groovy.json.JsonSlurper;

// Create a map of environment and namespace associated
def envNsMap = [
  "useast-duluth-nonprod-green-app": ["dev", "test"],
  "useast-duluth-nonprod-blue-app": ["dev", "test"],
  "useast-duluth-prod-green-app": ["preprod", "prod"],
  "useast-duluth-prod-blue-app": ["preprod", "prod"],
  "blue-sandbox": ["dev", "test"],
  "green-sandbox": ["dev", "test"]
]

def getPriURL() {
  try{
    List<String> repoList = new ArrayList<String>()
    
    URL apiUrl1 = "https://git01.pfsfhq.com/api/v3/orgs/pri/repos?access_token=6385b82adaaf13b0d1d96aa7b5a2a904ce9f7551".toURL()
    List json1 = new JsonSlurper().parse(apiUrl1.newReader())
    for (repo in json1) { 
      repoList.add('pri/'+repo.name) 
    }

    URL apiUrl2 = "https://git01.pfsfhq.com/api/v3/orgs/ClientPortal/repos?access_token=6385b82adaaf13b0d1d96aa7b5a2a904ce9f7551".toURL()
    List json2 = new JsonSlurper().parse(apiUrl2.newReader())
    for (repo in json2) { 
      repoList.add('ClientPortal/'+repo.name) 
    }
    
    URL apiUrl3 = "https://git01.pfsfhq.com/api/v3/orgs/NextGen/repos?access_token=6385b82adaaf13b0d1d96aa7b5a2a904ce9f7551".toURL()
    List json3 = new JsonSlurper().parse(apiUrl3.newReader())
    for (repo in json3) { 
      repoList.add('NextGen/'+repo.name) 
    }
    
    URL apiUrl4 = "https://git01.pfsfhq.com/api/v3/orgs/Reins/repos?access_token=6385b82adaaf13b0d1d96aa7b5a2a904ce9f7551".toURL()
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
    folder("Application_Deployments/${env.key}/${ns}/pri")
    folder("Application_Deployments/${env.key}/${ns}/ClientPortal")
    folder("Application_Deployments/${env.key}/${ns}/NextGen")
    folder("Application_Deployments/${env.key}/${ns}/Reins")
    
    for (repo in getPriURL()) {
      multibranchPipelineJob("Application_Deployments/${env.key}/${ns}/${repo}") {
        branchSources {
          branchSource {
            source {
              git {
                id(repo)
                remote('https://git01.pfsfhq.com/'+repo+'.git')
                credentialsId('a813d979-f746-4065-bd49-2a103dd6217d')
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
