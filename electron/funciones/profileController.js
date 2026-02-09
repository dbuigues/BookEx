document.addEventListener('DOMContentLoaded', () => {
    const registerSection = document.getElementById('registerSection');
    const loginSection = document.getElementById('loginSection');
    const userInfo = document.getElementById('userInfo');

    const welcomeUser = document.getElementById('welcomeUser');
    const userPFP = document.getElementById('userProfilePic');

    const btnCambiarAIniciar = document.getElementById('cambiarAIniciar');
    const btnCambiarARegistrar = document.getElementById('cambiarARegistrar');
    const btnCerrarSesion = document.getElementById('cerrarSesion');

    const usuario = sessionStorage.getItem('sesionActiva');

    if (!usuario) {
        registerSection.style.display = 'none';
        loginSection.style.display = 'block';
        userInfo.style.display = 'none';
        btnCerrarSesion.style.display = 'none';
    } else {
        registerSection.style.display = 'none';
        loginSection.style.display = 'none';
        userInfo.style.display = 'block';
        btnCerrarSesion.style.display = 'block';

        welcomeUser.innerText = `Bienvenido de nuevo, ${usuario}!`;
        userPFP.src = `https://bookex-u97b.onrender.com/api/usuarios/getpfp/${usuario}`;
        userPFP.alt = `Foto de perfil de ${usuario}`;
    }

    btnCambiarAIniciar.addEventListener('click', () => {
        loginSection.style.display = 'block';
        registerSection.style.display = 'none';
        userInfo.style.display = 'none';
        btnCerrarSesion.style.display = 'none';
    });

    btnCambiarARegistrar.addEventListener('click', () => {
        loginSection.style.display = 'none';
        registerSection.style.display = 'block';
        userInfo.style.display = 'none';
        btnCerrarSesion.style.display = 'none';
    });

    btnCerrarSesion.addEventListener('click', () => {
        sessionStorage.removeItem('sesionActiva');
        location.reload();
    });
});
