// Autore:              Eyad Issa
// Anno accademico:     2022/23

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <stdbool.h>
#include <signal.h>
#include <string.h>
#include <fcntl.h>

int write_pipe;

void stop_p0()
{
    close(write_pipe);
    printf("PID %d: SIGUSR1 recieved. Exiting.\n", getpid());
    exit(0);
}

void print_usage(char *prog_name)
{
    printf("Usage: %s Fin W T\n");
}

void execute_p1(int read_pipe)
{
    int old_count = 0;
    int turn = 0;
    int new_count;
    while (read(read_pipe, &new_count, sizeof(int)) == sizeof(int))
    {
        int delta = new_count - old_count;
        if (delta != 0)
        {
            turn++;
            printf("Lettura numero %d: %d caratteri %s\n", turn, delta > 0 ? delta : -delta, delta > 0 ? "in più" : "in meno");
        }
        old_count = new_count;
    }

    printf("Lettura terminata!");
}

void execute_p2(int p0_pid, int T)
{
    sleep(T);
    kill(p0_pid, SIGUSR1);
}

void main(int argc, char **argv)
{

    if (argc < 1)
    {
        fprintf(stderr, "Cannot start program without program name (argv[0])\n");
        exit(-1);
    }
    else if (argc < 4)
    {
        print_usage(argv[0]);
        exit(0);
    }

    char *Fin = argv[1];
    int T = atoi(argv[2]);
    int W = atoi(argv[3]);

    if (strlen(Fin) < 1)
    {
        fprintf(stderr, "Invalid path specified.\n");
        exit(-1);
    }
    else if (Fin[0] != '/')
    {
        fprintf(stderr, "The path must be absolute.\n");
        exit(-1);
    }
    else if (T < 0)
    {
        fprintf(stderr, "T must be positive.\n");
        exit(-1);
    }
    else if (W < 0)
    {
        fprintf(stderr, "W must be positive.\n");
        exit(-1);
    }

    int p0 = getpid();

    int number_pipe[2];
    if (pipe(number_pipe))
    {
        perror("Could not create pipe: ");
        exit(-1);
    }

    int p1 = fork();
    if (p1 < 0)
    {
        perror("Could not fork: ");
        exit(-1);
    }
    else if (p1 == 0)
    {
        // Close writing side
        close(number_pipe[1]);

        execute_p1(number_pipe[0]);
        close(number_pipe[0]);
        exit(0);
    }

    int p2 = fork();
    if (p2 < 0)
    {
        perror("Could not fork: ");
        exit(-1);
    }
    else if (p2 == 0)
    {
        // This doesn't need pipes
        close(number_pipe[0]);
        close(number_pipe[1]);

        execute_p2(p0, T);
        exit(0);
    }

    close(number_pipe[0]);
    signal(SIGUSR1, stop_p0);

    do
    {
        int fd = open(Fin, O_RDONLY);
        if (fd < 0)
        {
            perror("Could not open file: ");
            exit(-1);
        }

        int count = 0;
        char buf;
        while (read(fd, &buf, sizeof(char)) == sizeof(char))
        {
            count++;
        }

        write(number_pipe[1], &count, sizeof(int));

        sleep(W);

    } while (true);
}