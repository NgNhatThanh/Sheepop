export const BASE_API_URL = import.meta.env.VITE_BASE_API_URL
export const BASE_BE_URL = import.meta.env.VITE_BASE_BE_URL
export const MAX_IMAGE_SIZE = import.meta.env.VITE_MAX_IMAGE_SIZE
export const BASE_FE_URL = import.meta.env.VITE_BASE_FE_URL
export const GOOGLE_LOGIN_URL=`https://accounts.google.com/o/oauth2/auth?client_id=952950371733-a8t3ggkh8lmrqjavc54vd33qe7mg7ljp&redirect_uri=${BASE_FE_URL}/redirect/auth&response_type=code&scope=email%20profile`