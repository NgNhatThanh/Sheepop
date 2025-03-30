import { useState, useEffect } from "react";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import { BASE_API_URL } from "../../constants";
import { Link } from 'react-router-dom'

// const banners = [
//   { id: 1, image: "/banner1.png", alt: "Banner 1" },
//   { id: 2, image: "/banner2.png", alt: "Banner 2" },
//   { id: 3, image: "/banner3.png", alt: "Banner 3" },
// ];

export default function BannerSlider() {
  const [banners, setBanners] = useState([])
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isPaused, setIsPaused] = useState(false);

  const prevSlide = () => {
    setCurrentIndex(currentIndex === 0 ? banners.length - 1 : currentIndex - 1);
    resetAutoPlay();
  };

  const nextSlide = () => {
    setCurrentIndex(currentIndex === banners.length - 1 ? 0 : currentIndex + 1);
    resetAutoPlay();
  };

  const goToSlide = (index) => {
    setCurrentIndex(index);
    resetAutoPlay();
  };

  const resetAutoPlay = () => {
    setIsPaused(true);
    setTimeout(() => setIsPaused(false), 5000);
  };

  useEffect(() => {
    const fetchBanners = () => {
      fetch(`${BASE_API_URL}/v1/homepage/get_banners`)
        .then(res => res.json())
        .then(res => setBanners(res))
    }

    fetchBanners()
  }, [])

  useEffect(() => {
    if (isPaused) return; 
    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev === banners.length - 1 ? 0 : prev + 1));
    }, 3000); 

    return () => clearInterval(interval); 
  }, [isPaused]);

  return (
    <div>
      {banners.length > 0 ? (
        <div className="relative w-full h-80 max-w-6xl mx-auto overflow-hidden rounded-xs border-b border-gray-300">
          <div
            className="flex transition-transform duration-500 ease-in-out"
            style={{ transform: `translateX(-${currentIndex * 100}%)` }}
          >
            {banners.map((banner) => (
              <Link to={banner.redirectUrl}>
                <img
                  key={banner.id}
                  src={banner.imageUrl}
                  className="w-full flex-shrink-0"
                />
              </Link>
            ))}
          </div>

          <button
            onClick={prevSlide}
            className="cursor-pointer absolute left-2 top-1/2 -translate-y-1/2 bg-black/50 text-white p-2 rounded-full hover:bg-black/80 transition"
          >
            <FaChevronLeft className="w-6 h-6" />
          </button>

          <button
            onClick={nextSlide}
            className="cursor-pointer absolute right-2 top-1/2 -translate-y-1/2 bg-black/50 text-white p-2 rounded-full hover:bg-black/80 transition"
          >
            <FaChevronRight className="w-6 h-6" />
          </button>

          <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
            {banners.map((_, index) => (
              <button
                key={index}
                onClick={() => goToSlide(index)}
                className={`cursor-pointer w-3 h-3 rounded-full ${
                  index === currentIndex ? "bg-white" : "bg-gray-400"
                } transition`}
              />
            ))}
          </div>
        </div>
      ): (
        <>
        </>
      )}
    </div>
  );
}
