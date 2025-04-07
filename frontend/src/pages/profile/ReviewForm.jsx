import { useState } from "react";
import { AiFillStar, AiOutlineStar } from "react-icons/ai";
import { FiUpload } from "react-icons/fi";
import { FaTimes } from 'react-icons/fa'
import { uploadImage } from '../../util/UploadUtil'
import { toast } from "react-toastify";
import { fetchWithAuth } from "../../util/AuthUtil";
import { BASE_API_URL } from "../../constants";

export default function ReviewForm({ shopOrder, closeForm, onSuccess }) {
  const [reviews, setReviews] = useState(
    shopOrder.items.map((item) => ({
      orderItemId: item.id,
      rating: 0,
      content: "",
      mediaList: [],
    }))
  );

  const handleRatingChange = (orderItemId, rating) => {
    setReviews((prev) =>
      prev.map((review) =>
        review.orderItemId === orderItemId ? { ...review, rating } : review
      )
    );
  };

  const handleTextChange = (orderItemId, text) => {
    setReviews((prev) =>
      prev.map((review) =>
        review.orderItemId === orderItemId ? { ...review, content: text } : review
      )
    );
  };

  const handleImageChange = async (orderItemId, e) => {
    const files = Array.from(e.target.files);
    const reviewToUpdate = reviews.find(review => review.orderItemId === orderItemId);
    if (!reviewToUpdate) return;
    const currentMediaList = [...reviewToUpdate.mediaList];
    if(files.length + currentMediaList.length > 5){
      toast.error("Tối đa 5 ảnh")
      return
    }

    setReviews((prev) =>
      prev.map((review) =>
        review.orderItemId === orderItemId
          ? { ...review, mediaList: [...review.mediaList, ...files.map(() => ({ url: "", type: "LOADING" }))] }
          : review
      )
    );

    const uploadedImages = await Promise.all(
      files.map(async (file) => {
        const url = await uploadImage(file);
        return { url, type: "IMAGE" };
      })
    );

    setReviews((prev) =>
      prev.map((review) =>
        review.orderItemId === orderItemId
          ? {
              ...review,
              mediaList: review.mediaList
                .slice(0, review.mediaList.length - files.length) 
                .concat(uploadedImages), 
            }
          : review
      )
    );
  };

  const handleRemoveMedia = (orderItemId, index) => {
    setReviews((prev) =>
      prev.map((review) =>
        review.orderItemId === orderItemId
          ? {
              ...review,
              mediaList: review.mediaList.filter((_, i) => i !== index)
            }
          : review
      )
    );
  }

  const handleCreateReview = () => {
    for(const review of reviews){
      if(review.rating === 0){
        toast.warning("Vui lòng đánh giá sao cho tất cả sản phẩm")
        return
      }
    }
    const body = {
      "shopOrderId": shopOrder.id,
      'itemReviews': reviews
    }
    fetchWithAuth(`${BASE_API_URL}/v1/review/create_review`, window.location, true, {
      method: "POST",
      headers: {
        "content-type": "application/json"
      },
      body: JSON.stringify(body)
    })
      .then(res => {
        if(res.ok){
          onSuccess()
          closeForm()
        }
        else toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
      })
      .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau!"))
  }

  return (
    <div className="fixed inset-0 z-10 flex items-center justify-center bg-gray-100/85">
      <div className="bg-white w-150 p-6 rounded-md overflow-y-auto h-3/4">
        <h2 className="text-xl font-semibold mb-4">Đánh giá sản phẩm</h2>
        {shopOrder.items.map((item) => (
          <div key={item.id} className="border-b p-4">
            <div className="flex items-center">
              <img src={item.product.thumbnailUrl} alt={item.product.name} className="w-16 h-16 object-cover rounded-lg" />
              <div className="ml-4 flex-1">
                <p className="font-medium text-gray-800">{item.product.name}</p>
                <div className="flex gap-5">
                  <div>
                    {item.attributes?.map((attr) => (
                      <p key={attr.name} className="text-gray-500 text-sm">
                          {attr.name}: {attr.value} &nbsp;
                      </p>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            <div className="flex space-x-1 my-4">
              {[...Array(5)].map((_, index) => (
                <span
                  key={index}
                  className="cursor-pointer"
                  onClick={() => handleRatingChange(item.id, index + 1)}
                >
                  {index + 1 <= reviews.find((r) => r.orderItemId === item.id)?.rating ? (
                    <AiFillStar className="text-yellow-500 text-2xl" />
                  ) : (
                    <AiOutlineStar className="text-gray-400 text-2xl" />
                  )}
                </span>
              ))}
            </div>

            <textarea
              className="w-full p-3 border rounded-md focus:outline-none focus:ring focus:ring-blue-300"
              rows="3"
              placeholder="Nhập nội dung đánh giá..."
              value={reviews.find((r) => r.orderItemId === item.id)?.content || ""}
              onChange={(e) => handleTextChange(item.id, e.target.value)}
            />

            <label className="flex items-center space-x-2 cursor-pointer text-blue-500 mt-3">
              <FiUpload />
              <span>Thêm hình ảnh (Tối đa: 5)</span>
              <input
                type="file"
                accept="image/*"
                multiple
                className="hidden"
                onChange={(e) => handleImageChange(item.id, e)}
              />
            </label>

            <div className="mt-2 flex space-x-2 overflow-x-auto">
              {reviews.find((r) => r.orderItemId === item.id)?.mediaList.map((media, index) => (
                <div key={index} className="w-16 h-16 flex items-center justify-center bg-gray-200 rounded relative">
                  {media.type === "LOADING" ? (
                    <span className="animate-pulse text-gray-500">⏳</span>
                  ) : (
                    <>
                      <img src={media.url} alt="Uploaded" className="w-full h-full object-cover rounded" />
                      <button
                        className="absolute top-0 right-0 bg-red-500 text-white text-xs p-1 rounded-full cursor-pointer hover:bg-red-600"
                        onClick={() => handleRemoveMedia(item.id, index)}
                      >
                        <FaTimes />
                      </button>
                    </>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}

        <div className="flex gap-4 mt-4">
          <button 
            className="w-3/4 bg-blue-500 text-white py-2 rounded-md cursor-pointer hover:bg-blue-600"
            onClick={handleCreateReview}
          >
            Gửi đánh giá
          </button>
          <button 
            className="w-1/4 bg-gray-300 text-white py-2 rounded-md cursor-pointer hover:bg-gray-400" 
            onClick={closeForm}
          >
            Hủy
          </button>
        </div>
      </div>
    </div>
  );
}
