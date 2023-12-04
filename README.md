# **Distributed Sorting using Ice and Java** 💻

Developed by: 

- Diana Balanta
- Carlos Bolaños
- Danna Espinosa
- Cristian Perafan
- Samuel Soto

## **Goal**

Develop a distributed sorting application using Ice and Java. The application will be composed of a client and a server. The client will ask for a file to be sorted and the server will sort the file and save it in a new file.

## **Components**

1. **Client**: The client will be responsible for asking the user for the file to be sorted.
2. **Server**: The server will be responsible for receiving the file, sorting it and saving it in a new file.
3. **Worker**: The worker will be responsible for sorting the file. The server will have a pool of workers that will be responsible for sorting the file. The server receives a lines of the file. The worker sorts the lines and returns the sorted lines to the server. 

## **Remotes Machines**

| Component| Remote PC Name |
|----------|----------|
|   Master   |   hgrid1   |
|   Client   |   hgrid4   |
|   Worker 1   |   hgrid10   |
|   Worker 2  |   hgrid11   |
|   Worker 3  |   hgrid13   |
|   Worker 4   |  hgrid15   |
|   Worker 5   |   hgrid18   |
|   Worker 6   |   hgrid2   |
|   Worker 7   |   hgrid3   |
|   Worker 8   |   hgrid5   |
|   Worker 9   |   hgrid9   |
|   Worker 10   |   hgrid8   |

** initial Aprroach**

Initially,the server must be started and then ther workers. Each worker must be register in the server dinamically. The client must be started and then the user must enter the file to be sorted.

## **Deployment** 🚀

1. In each Remote PC, open a terminal and created a directory called **Datamining** and enter in this directory.

2. Once all the directories are created, copy the files from the **buiild/libs** of the project, depending on the module you want to start on the machine.

3. In the folder **Datamining** in the server and workers must be also the file you want to sort.

3. In the server, execute the following command:

   ```bash
   java -jar master.jar 
   ```
   Server configuration file:
   ```bash
    Callback.Server.Endpoints=default -h * -p 9091
    
    Ice.Warn.Connections=1
   ```

4. In the workers, execute the following command:
   ```bash
   java -jar worker.jar 
   ```
    File of worker configuration:

    ```bash
    CallbackSender.Proxy=callbackSender:default -h hgrid1 -p 9091
    Callback.Worker.Endpoints=default -h * -p 9092
    Ice.Warn.Connections=1
    ```
5. In the client, execute the following command:
    ```bash
   java -jar client.jar 
   ```
   File of client configuration:
    ```bash
    CallbackSender.Proxy=callbackSender:default -h hgrid1 -p 9091
    Callback.Client.Endpoints=default -h *
    Ice.Warn.Connections=1
   ```
   
4. In the client, enter the name of the file to be sorted, in the next format:
   ```bash
   dist_sorter:<filename>.txt
   ```
