import { BASE_API_URL, MAX_IMAGE_SIZE } from "../constants";
import { fetchWithAuth } from "./AuthUtil";

export async function uploadImage(file) {
    if (!file || !file.type.startsWith('image/')){
        alert("File's type is invalid")
        return
    }
    if(file.size > MAX_IMAGE_SIZE){
        alert("File's size is too large (> 1MB)")
        return
    }
    const formData = new FormData()
    formData.append('file', file)
    const res = await fetchWithAuth(`${BASE_API_URL}/v1/upload/image`, null, true, {
        method: "POST",
        body: formData
    })
    if(res.status === 200){
        const url = await res.json()
                    .then(data => data.url)
        return url
    }
    else{
        await res.json()
            .then(data => alert(data.message))
        return
    }
}