#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <limits.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/un.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <signal.h>
#include <poll.h>
#include <arpa/inet.h>
#include<iostream>
using namespace std;
#define POLL_SIZE 32
#define LISTEN_QUEUE 5
#define SOCKET_PATH "web/server_socket.sock"

 
#define TRUE 1
#define FALSE 0
 

 
int main()
{
    int server_sockfd, client_sockfd;
    int server_len, client_len;
    struct sockaddr_in server_address;
    struct sockaddr_in client_address;
    int result;
 
    struct pollfd poll_set[POLL_SIZE];
    int numfds = 0;
    
    server_sockfd = socket(PF_INET, SOCK_STREAM, 0);
 
    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(5533);
    server_address.sin_addr.s_addr = htons(INADDR_ANY);
    

    server_len = sizeof(server_address);
 
 
    bind(server_sockfd, (struct sockaddr *)&server_address, (socklen_t)server_len);
    listen(server_sockfd, 5);
    memset(poll_set, '\0', sizeof(poll_set));
    poll_set[0].fd = server_sockfd;
    poll_set[0].events = POLLIN;
    numfds++;
 	
	//int read;//
        int fd_index;
        int nread;
 	string data;
	printf("Waiting for client ...\n");
    while (1) {
    
       	
        //result = select( (max_fd + 1), &testfds, (fd_set *)0, (fd_set *)0, (struct timeval *) 0);
        poll(poll_set, numfds, -1);
        for(fd_index = 0; fd_index < numfds; fd_index++)
        {
            if( poll_set[fd_index].revents & POLLIN ) {
                if(poll_set[fd_index].fd == server_sockfd) {
                    client_len = sizeof(client_address);
                    client_sockfd = accept(server_sockfd, (struct sockaddr *)&client_address, (socklen_t *)&client_len);
 
                    poll_set[numfds].fd = client_sockfd;
                    poll_set[numfds].events = POLLIN;
                    numfds++;
                    printf("Adding client on fd %d\n", client_sockfd);
                }
                else {
                    ioctl(poll_set[fd_index].fd, FIONREAD, &nread);
                    if( nread == 0 )
                    {
                        close(poll_set[fd_index].fd);
                        poll_set[fd_index].events = 0;
                        printf("Removing client on fd %d\n", poll_set[fd_index].fd);
                        int i = fd_index;
                        //for( i = fd_index; i<numfds; i++)
                        //{
                        poll_set[i] = poll_set[i + 1];
                        //}
                        numfds--;
                    }
 
 
                    else {
                        
                        unsigned char BYTE;
                        read(poll_set[fd_index].fd, &BYTE , sizeof(BYTE));
                        cout<<BYTE;


                        // char *tan;
                        // read(poll_set[fd_index].fd, tan, 5);
                        // cout<<tan;

			            // char* data;
                        // strcpy(data,"heeelo noname");
                        //send(poll_set[fd_index].fd, data,sizeof(data),  0);
                    }
                }
            }
 
 
 
 
        }
 
 
 
    }
}
