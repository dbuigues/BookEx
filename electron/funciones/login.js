const API_LOG_URL = 'http://localhost:8080/api/usuarios/login';


async function login(userToLogin) {
    
    console.log('Agregando usuario:', userToLogin);
    console.log('JSON enviado:', JSON.stringify(userToLogin));
    const errorDiv = document.getElementById('LoginError');
    try {
        const response = await fetch(`${API_LOG_URL}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userToLogin),
        });
        console.log(userToLogin);

        const data = await response.json();

        if (response.ok && data.statusCodeValue === 200) {
            errorDiv.innerText = `Sesión iniciada con éxito.`;
            console.log(data);
            document.getElementById('logEmail').value = '';
            document.getElementById('logPassword').value = '';
            sessionStorage.setItem('sesionActiva', data.body);
            console.log(sessionStorage.getItem('sesionActiva'));
        } else {
            console.log('Error al iniciar sesion', data);
            errorDiv.innerText = `Error: Correo o contraseña incorrectos.`;
        }

    } catch (error) {
        console.log('Error de conexión:', error.message);
        errorDiv.innerText = `Error: Correo o contraseña incorrectos.`;
    }
}
const LogUser = document.getElementById('LogUserButton')

LogUser.onclick = async (e) => {
    e.preventDefault();
    const intento = {
        correo: document.getElementById('logEmail').value,
        contrasena: document.getElementById('logPassword').value,
    };
    await login(intento);
}

