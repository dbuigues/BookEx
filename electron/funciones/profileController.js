document.addEventListener('DOMContentLoaded', function() {
        const registerSection = document.getElementById('registerSection');
        const loginSection = document.getElementById('loginSection');
        const userInfo = document.getElementById('userInfo');
        const welcomeUser = document.getElementById('welcomeUser');

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
    }
});