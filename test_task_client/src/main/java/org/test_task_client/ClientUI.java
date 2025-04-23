package org.test_task_client;

import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class ClientUI {
    private final Network network;

    public void start(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            String input = scanner.nextLine();
            network.sendMsg(input);
        }
    }

    public static void main(String[] args) {
        Network network1 = new Network();
        ClientUI clientUI = new ClientUI(network1);
        clientUI.start();
    }
}
