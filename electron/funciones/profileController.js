document.addEventListener('DOMContentLoaded', async function() {
        const registerSection = document.getElementById('registerSection');
        const loginSection = document.getElementById('loginSection');
        const userInfo = document.getElementById('userInfo');
        const welcomeUser = document.getElementById('welcomeUser');
        const userPFP = document.getElementById('userProfilePic');

    if (!sessionStorage.getItem('sesionActiva')) {
        console.log('No hay sesión activa.');

        registerSection.style.display = 'none';
        loginSection.style.display = 'block';
        userInfo.style.display = 'none';

    }else{
        console.log(`Sesión activa de ${sessionStorage.getItem('sesionActiva')}`);
        registerSection.style.display = 'none';
        loginSection.style.display = 'none';
        userInfo.style.display = 'block';
        welcomeUser.innerText = `Bienvenido de nuevo, ${sessionStorage.getItem('sesionActiva')}!`;

        userPFP.src = `https://bookex-u97b.onrender.com/api/usuarios/getpfp/${sessionStorage.getItem('sesionActiva')}`;
        userPFP.alt = `Foto de perfil de ${sessionStorage.getItem('sesionActiva')}`;
    }
});

document.getElementById("cambiarAIniciar").addEventListener("click",()=>{
    loginSection.style.display = 'block';
    registerSection.style.display = 'none';
    userInfo.style.display = 'none';
});

document.getElementById("cambiarARegistrar").addEventListener("click",()=>{
    loginSection.style.display = 'none';
    registerSection.style.display = 'block';
    userInfo.style.display = 'none';
});