import { useEffect, useState } from "react"
import { BASE_API_URL } from "../../../constants"
import { fetchWithAuth } from "../../../util/AuthUtil"

export default function BannerManage(){

    const [banners, setBanners] = useState([])

    useEffect(() => {
        const fetchBanners = () => {
            fetchWithAuth(`${BASE_API_URL}/v1/admin/content/get_banners`)
                .then(res => res.json())
                .then(res => setBanners(res))
        }

        fetchBanners()
    }, [])

    return (
        <div>
            <h2
                className="font-semibold text-2xl"
            >
                Danh sách banner
            </h2>
            <div>
                {banners.map((banner, index) => (
                    <div className="flex gap-3">
                        <p>{index + 1}</p>
                        <img
                            src={banner.imageUrl}
                            className="w-20 rounded-sm"
                        />
                        <p>{banner.redirectUrl}</p>
                    </div>  
                ))}
            </div>
        </div>
    )
}