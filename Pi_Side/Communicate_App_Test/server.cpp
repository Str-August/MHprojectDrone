#include <iostream>
#include<string.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<stdlib.h>
#include<unistd.h>

using namespace std;

int main()
{
    int client, server;
    int portnum = 5533;
    bool isExit = false;
    int bufsize = 1024;
    char buffer[bufsize];

    struct sockaddr_in server_addr;
    socklen_t size;

    //init socekt

    client = socket(AF_INET, SOCK_STREAM, 0);

    if(client < 0){
        cout << "Error Estableciendo la conexion" << endl;
    }

    cout <<"server Socket conexion creada" << endl;

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htons(INADDR_ANY);
    server_addr.sin_port = htons(portnum);


    //biding soket

    if(bind(client,(struct sockaddr*)&server_addr,sizeof(server_addr)) < 0){
        cout << "Error Biding Socket" << endl;
        exit(1);
    }

    size= sizeof(server_addr);
    cout << "buscando clientes" << endl;

    //escuchando sokets

    listen(client,1);

    //accept client

    server = accept(client, (struct sockaddr*)&server_addr, &size);

    if(server < 0){
        cout<< "Error al Aceptar" << endl;
        exit(1);
    }

    while(server >0){
        strcpy(buffer,"server conectado---\n");
        send(server,buffer,bufsize,0);

        cout <<"conectado con el cliente" << endl;
        cout << "Ingresad # paara terminar la conexion" << endl;

        cout <<"client: ";
        do{
            recv(server,buffer,bufsize,0);
            cout << "buffer" << " ";
            if(*buffer == '#'){
                *buffer = '*';
                isExit=true;
            }
        }while(*buffer != '*');

        do{
            cout << "\n server: ";
            do{
                cin >> buffer;
                send(server,buffer,bufsize,0);
                if(*buffer == '#'){
                    send(server,buffer,bufsize,0);
                    *buffer = '*';
                    isExit=true;
                }
            }while(*buffer != '*');

            cout << "Client: ";

            do{
                recv(server,buffer,bufsize,0);
                cout << buffer << " ";
                if(*buffer == '#'){
                    *buffer = '*';
                    isExit = true;
                }
            }while(*buffer != '*');
        }while(isExit);

        cout << "Conection Terminated..." << endl;
        cout << "Goodbye..." << endl;
        isExit =false;
        exit(1);
    }
    close(client);
    return 0;
}
          