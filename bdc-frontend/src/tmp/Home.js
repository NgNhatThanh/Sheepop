function Home(){
    return (
        <div>
            <a href="https://accounts.google.com/o/oauth2/auth?client_id=952950371733-a8t3ggkh8lmrqjavc54vd33qe7mg7ljp&redirect_uri=http://localhost:3000/redirect/auth&response_type=code&scope=email%20profile">
            {/* <a href="http://localhost:3000/redirect/auth?abc=5">  */}
                <button>
                    Log in with Google
                </button>
            </a>
        </div>
    )
}

export default Home;