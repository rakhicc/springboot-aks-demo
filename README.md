# a simple springboot app to say hello which is created mainly to learn how to deploy a java springboot application using azure resources ACR and AKS
# Run following commands in intellij idea terminal
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
