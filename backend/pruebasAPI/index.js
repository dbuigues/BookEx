// AÑADIR USUARIO A LA API REST

async function agregarUsuario(userToAdd) {
    console.log('Agregando usuario:', userToAdd);
    console.log('JSON enviado:', JSON.stringify(userToAdd));

  try {
    const response = await fetch('http://localhost:8080/api/usuarios', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },      
      body: JSON.stringify(userToAdd),
    });
    console.log(userToAdd);

    const data = await response.json();

    if (response.ok) {
      console.log('Usuario creado exitosamente:');
      console.log(data);
    } else {
      console.error('Error al crear usuario:', data);
    }

  } catch (error) {
    console.error('Error de conexión:', error.message);
  }
}

// Convertir imagen de input file a base64
function imagenABase64(inputFile) {
  return new Promise((resolve, reject) => {
    const file = inputFile.files[0];
    if (!file) {
        console.log("No se ha seleccionado ningún archivo.");
      resolve(null);
      return;
    }
    const reader = new FileReader();
    reader.onload = function(e) {
      // e.target.result es un string dataURL: "data:image/png;base64,..."
      // Extraemos solo la parte base64
      const base64 = e.target.result.split(',')[1];
      resolve(base64);
    };
    reader.onerror = function(error) {
      reject(error);
    };
    reader.readAsDataURL(file);
  });
}

const addUsuario = document.getElementById('addUsuario');
addUsuario.onclick = async (event) => {
  event.preventDefault(); // Evitar que el formulario recargue la página

  const fotoInput = document.getElementById('fotoPerfil');
  const fotoBase64 = await imagenABase64(fotoInput);

  const usuario = {
    nombre: document.getElementById('nombre').value,
    correo: document.getElementById('correo').value,
    contrasena: document.getElementById('contrasena').value,
    fotoPerfil: fotoBase64
  };

  await agregarUsuario(usuario);
};
