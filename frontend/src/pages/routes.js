import { lazy } from "react";

const HomePage = lazy(() => import("./home/index.tsx"))
const LoginPage = lazy(() => import("./login/index.tsx"))
const RegisterPage = lazy(() => import("./register/index.tsx"))
const HandleRedirect = lazy(() => import("./login/HandleOauthRedirect.js"))

export default [
    {
        path: "/",
        exact: true,
        public: true,
        component: HomePage
    },
    {
        path: '/login',
        exact: true,
        public: true,
        component: LoginPage
    },
    {
        path: '/register',
        exact: true,
        public: true,
        component: RegisterPage
    },
    {
        path: '/redirect/:target',
        exact: false,
        public: true,
        component: HandleRedirect
    }
]