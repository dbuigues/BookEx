document.addEventListener('DOMContentLoaded', async function() {
        const registerSection = document.getElementById('registerSection');
        const loginSection = document.getElementById('loginSection');
        const userInfo = document.getElementById('userInfo');
        const welcomeUser = document.getElementById('welcomeUser');
        const userPFP = document.getElementById('userProfilePic');

    if (!sessionStorage.getItem('sesionActiva')) {
        console.log('No hay sesión activa.');

        registerSection.style.display = 'block';
        loginSection.style.display = 'block';
        userInfo.style.display = 'none';

    }else{
        console.log(`Sesión activa de ${sessionStorage.getItem('sesionActiva')}`);
        registerSection.style.display = 'none';
        loginSection.style.display = 'none';
        userInfo.style.display = 'block';
        welcomeUser.innerText = `Bienvenido de nuevo, ${sessionStorage.getItem('sesionActiva')}!`;

        userPFP.src = `http://localhost:8080/api/usuarios/getpfp/${sessionStorage.getItem('sesionActiva')}`;
        userPFP.alt = `Foto de perfil de ${sessionStorage.getItem('sesionActiva')}`;
    }
});