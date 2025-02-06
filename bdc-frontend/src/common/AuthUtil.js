import { BASE_API_URL } from "../constants"

export async function checkAuthenticated(){
    var ok = false
    try{
        const accessToken = localStorage.getItem("access_token")
        if(!accessToken) throw new Error("Access token missing")
        const res = await fetch(`${BASE_API_URL}/auth/ping`, {
            headers:{
                Authorization: `Bearer ${accessToken}`
            }
        })
        if(res.status !== 200) throw new Error("Cannot ping")
        ok = true
    }
    catch (err){
        try{
            const ref = await fetch(`${BASE_API_URL}/auth/refreshToken`, {
                credentials: "include"
            })
            if(ref.status !== 200) ok = false
            else{
                await ref.json()
                .then(data => {
                    localStorage.setItem("access_token", data.token)
                    ok = true
                })
            }
        }
        catch(error){
            ok = false
        }
    }
    return ok
}

export async function fetchWithAuth(url, options = {}){
    const res = await fetch(url, {
        ...options,
        headers:{
            ...options.headers,
            'Authorization': `Bearer ${localStorage.getItem("access_token")}`,
        }
    });
    if(!res.ok){
        if(res.status === 401){
            const ref = await fetch(`${BASE_API_URL}/auth/refreshToken`, {
                credentials: "include"
            })
            if(ref.status !== 200){
                window.location.href = '/login'
            }
            else{
                const resJson = await ref.json()
                localStorage.setItem("access_token", resJson.token)
                return fetch(url, {
                    ...options,
                    headers:{
                        ...options.headers,
                        'Authorization': `Bearer ${localStorage.getItem("access_token")}`,
                    }
                });
            }
        }
        else{
            console.log("Unknown error fethWithAuth")
        }
    }
    else{
        console.log(res)
        return res
    }
}