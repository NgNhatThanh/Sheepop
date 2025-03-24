import { Stomp } from "@stomp/stompjs";
import { createContext, useEffect, useState } from "react";

const SocketContext = createContext()

export function WebsocketProvider({ children }){

    const [stompClient, setStomptClient] = useState(null)
    const [isConnected, setIsConnected] = useState(false)
    
    useEffect(() => {
        const client = Stomp.client("http://localhost:8080/ws");
        client.reconnectDelay = 5000;
        // client.debug = () => {}
        const accessToken = localStorage.getItem("access_token")
        client.connect({
            Authorization: `Bearer ${accessToken}`
        }, () => {
            setIsConnected(true)
            console.log("WS connected")
        }, (e) => {
            console.log("WS connect error: ")
            console.log(e)
        })
        setStomptClient(client)

        // return () => client.deactivate() 
    }, [])

    const subscribeToChanel = (chanel, func) => {
        if(stompClient === null || !isConnected) {
            return
        }
        stompClient.subscribe(chanel, (mes) => {
            func(JSON.parse(mes.body))
        })
    }

    return (
        <SocketContext.Provider value={{isConnected, stompClient, subscribeToChanel}}>
            {children}
        </SocketContext.Provider>
    )

}

export default SocketContext;