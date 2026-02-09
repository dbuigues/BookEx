const BotonAgregarLista = document.getElementById('agregar-lista');
const BotoncerrarModal = document.getElementById('cerrar');
const listasContainer = document.getElementById('listas-container');
const modal = document.getElementById('modal');
const BotonCrearLista = document.getElementById('crear-lista');
let sesion;

const verListas = async (sesion) => {
  try {
    const response = await fetch(`https://bookex-u97b.onrender.com/api/listas/usuario/bycorreo/${encodeURIComponent(sesion)}`);
    const data = await response.json();
    console.log("Datos recibidos:", data);
    console.log("Cargando listas...");
    
    const container = document.getElementById('listas-container');
    if (!container) {
      console.error("No se encontró el elemento listas-container");
      return;
    }
    
    container.innerHTML = '';
    
    // Verificar si data es un array directamente o tiene una propiedad items
    const listas = Array.isArray(data) ? data : (data.items || []);
    console.log("Listas a mostrar:", listas);
    
    if (listas.length > 0) {
      listas.forEach(lista => {
        const div = document.createElement('div');
        div.className = 'lista-item';
        div.textContent = lista.nombreLista;
        container.appendChild(div);
      });
    }
  } catch (error) {
    console.error(`Error al buscar listas del usuario:`, error);
    const container = document.getElementById('listas-container');
    if (container) {
      container.innerHTML = '<p>Error al cargar las listas.</p>';
    }
  }
}

const crearLista = async (nombreDeLista, sesion) => {

  try {
    const idCall = await fetch(`https://bookex-u97b.onrender.com/api/usuarios/correo/${encodeURIComponent(sesion)}`);
    const userData = await idCall.json();
    const id = userData.idUsuario;
    console.log("Datos recibidos para crear lista:", id);

    const lista = {
      nombreLista: nombreDeLista, 
      idUsuario: id
    };
    const response = await fetch('https://bookex-u97b.onrender.com/api/listas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(lista)
    });
    if (response.ok) {
      console.log("Lista creada exitosamente");
      modal.style.display = 'none';
      verListas(sesion);
    }
  } catch (error) {
    console.error('Error al crear la lista:', error);
  }
}


if (sessionStorage.getItem('sesionActiva')!=null) {
  sesion = sessionStorage.getItem('sesionActiva');
  BotonAgregarLista.style.display = 'block';
  verListas(sesion);
}else{
  BotonAgregarLista.style.display = 'none';
}

BotonAgregarLista.addEventListener('click', () => { modal.style.display = 'block'; });
BotoncerrarModal.addEventListener('click', () => { modal.style.display = 'none'; });
BotonCrearLista.addEventListener('click', (event) => {
  event.preventDefault();
  crearLista(document.getElementById('nombre-lista').value, sesion);
});
