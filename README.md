# a springboot app which will save user information to azure sql server, this project is created mainly to learn how to deploy a java springboot application using azure resources ACR and AKS
# docker image is built using azure container registry
# dev and test profiles are created and seperate DB is created using azure sql server for dev and test environment
# Aks will control which profile to run
# jwt authentication is also enabled
Created the project in intellij idea snd executed commands as explained below:
Run following commands in intellij idea terminal
az login


az acr update --name Rakhiacr12345 --role-assignment-mode rbac
az acr build --registry Rakhiacr12345 --image springboot-aks-demo:v1 --platform linux/amd64 .
then create AKS using below command, as I dont have rights to assign role, ACR cannot be attached while creating ACR(If Acr is attached while creating AKS images can be pulled automatically from ACR)
az aks create --resource-group RG_RakhiChirayilLab --name myAKSCluster --node-count 1 --generate-ssh-keys
then use azure CLI in bash mode, run the command that connect to AKS cluster
az aks get-credentials --resource-group RG_RakhiChirayilLab --name myAKSCluster
to see your kubectl node run below command:
kubectl get nodes
then run below command where password is obtained using az acr credential show --name Rakhiacr12345
kubectl create secret docker-registry acr-secret \
  --docker-server=rakhiacr12345.azurecr.io \
  --docker-username=Rakhiacr12345 \
  --docker-password=AZphLQUfzXV7b86j6ov1AH22v5ezzCTrdE1U4av9bRhPts5MpoJHJQQJ99CEACi5YpzEqg7NAAACAZCRiF8f
then create deployment and service yml file in project root and in azure cli
git clone project then  cd springboot-aks-demo
then ls 
then run like below:
chandran [ ~/springboot-aks-demo ]$ ls
Dockerfile  k8s  pom.xml  src
chandran [ ~/springboot-aks-demo ]$ kubectl apply -f k8s/
deployment.apps/springboot-app created
service/springboot-service created

Check pods:
kubectl get pods
chandran [ ~/springboot-aks-demo ]$ kubectl get pods
NAME                              READY   STATUS             RESTARTS   AGE
springboot-app-65754b9bc5-xrjmc   0/1     ImagePullBackOff   0          2m27s
chandran [ ~/springboot-aks-demo ]$ kubectl get service springboot-service
NAME                 TYPE           CLUSTER-IP    EXTERNAL-IP       PORT(S)        AGE
springboot-service   LoadBalancer   10.0.171.22   135.236.129.136   80:32603/TCP   2m59s

http://135.236.129.136/rakhi will give output as Hello rakhi



# Working with two profiles dev and test with azure sql server and ACR and AKS
Big Picture (What We’ll Do)
Spring Boot Profiles → dev / test
        ↓
Store DB credentials → Kubernetes Secrets
        ↓
Build image → ACR (cloud)
        ↓
Deploy → AKS (dev + test separately)

No changes are needed for dockerfile as one image is being build for all environment and profile based switching is managed by Kubernetes.
created application-dev yaml and test yaml separately as well as service and deployment yaml files separately for each environment.
create ACR using command in azure cli:
az acr create \
  --resource-group RG_RakhiChirayilLab \
  --name rakhiacr12345 \
  --sku Basic


create AKS cluster:
az aks create \
  --resource-group RG_RakhiChirayilLab \
  --name rakhi-aks-cluster \
  --node-count 1 \
  --enable-addons monitoring \
  --generate-ssh-keys
connect to the cluster :
az aks get-credentials \
  --resource-group RG_RakhiChirayilLab \
  --name rakhi-aks-cluster \
  --overwrite-existing
since aks cannot connect to acr due to lacking od admin rights, manually connect aks to acr using below commands:
az acr update --name rakhiacr12345 --admin-enabled
get password using below command 
az acr credential show --name rakhiacr12345
Step 2: Create Kubernetes Secret
kubectl create secret docker-registry acr-secret \
--docker-server=rakhiacr12345.azurecr.io \
--docker-username=rakhiacr12345 \
--docker-password= 6M0nUyYDavyAClvcPGa8msWn2VxcP04n6XpeNc0GLka5HxoV4HLwJQQJ99CFACi5YpzEqg7NAAACAZCRNeDs

Step 3: Use Secret in Deployment files

now create secrets for storing db username and password so that no values are added in code directly.
kubectl create secret generic db-secret-dev \
  --from-literal=DB_USERNAME=sqladmin@springboot-sql-server\
  --from-literal=DB_PASSWORD=YourStrongPassword123!
Create TEST secret
kubectl create secret generic db-secret-test \
--from-literal=DB_USERNAME=sqladmin@springboot-sql-server\
--from-literal=DB_PASSWORD=YourStrongPassword123!

Now secrets are stored securely in AKS
now in intellij idea terminal do mvn clean package
then build image using command:
az acr build  --registry rakhiacr12345  --image springboot-aks-demo:v1 .

then add the db secrets in deployment files of dev and test
commit to github and then in azure cli connect to aks cluster and then clone project then run below commands:
chandran [ ~ ]$ cd springboot-aks-demo
chandran [ ~/springboot-aks-demo ]$ ls
Dockerfile  k8s  pom.xml  README.md  src
chandran [ ~/springboot-aks-demo ]$ kubectl apply -f deployment-dev.yaml
error: the path "deployment-dev.yaml" does not exist
chandran [ ~/springboot-aks-demo ]$ cd k8s
chandran [ ~/springboot-aks-demo/k8s ]$ kubectl apply -f deployment-dev.yaml
deployment.apps/springboot-dev created
chandran [ ~/springboot-aks-demo/k8s ]$ kubectl apply -f deployment-test.yaml
deployment.apps/springboot-test created
chandran [ ~/springboot-aks-demo/k8s ]$ kubectl apply -f deployment-test.yaml
deployment.apps/springboot-test created
chandran [ ~/springboot-aks-demo/k8s ]$ kubectl apply -f service-dev.yaml
service/springboot-dev-service created

chandran [ ~/springboot-aks-demo/k8s ]$ kubectl apply -f service-test.yaml
service/springboot-test-service created
chandran [ ~/springboot-aks-demo/k8s ]$ kubectl get svc
NAME                      TYPE           CLUSTER-IP     EXTERNAL-IP     PORT(S)        AGE
kubernetes                ClusterIP      10.0.0.1       <none>          443/TCP        50m
springboot-dev-service    LoadBalancer   10.0.130.129   4.210.54.145    80:32647/TCP   32s
springboot-test-service   LoadBalancer   10.0.172.78    20.67.162.252   80:32282/TCP   14s

restart the pod using below command in case you change the secret:
kubectl rollout restart deployment springboot-test
successfully tested in test env:
<img width="592" height="245" alt="springboot_test" src="https://github.com/user-attachments/assets/20b30970-0695-46b2-af71-37400241002a" />

successfully tested in dev env:
<img width="482" height="247" alt="springboot-dev" src="https://github.com/user-attachments/assets/3b53e22c-ab9d-4e7b-abf1-def86d21474b" />




