const BotonCrearLista = document.getElementById('agregar-lista');
const BotoncerrarModal = document.getElementById('cerrar');
const listasContainer = document.getElementById('listas-container');
const modal = document.getElementById('modal');
let sesion;

const verListas = async (sesion) => {
  try {
    const response = await fetch(`http://localhost:8080/api/listas/usuario/bycorreo/${encodeURIComponent(sesion)}`);
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
    } else {
      container.innerHTML = '<p>No tienes listas creadas.</p>';
    }
  } catch (error) {
    console.error(`Error al buscar listas del usuario:`, error);
    const container = document.getElementById('listas-container');
    if (container) {
      container.innerHTML = '<p>Error al cargar las listas.</p>';
    }
  }
}

const crearLista = async (nombreLista, sesion) => {}


if (sessionStorage.getItem('sesionActiva')!=null) {
  sesion = sessionStorage.getItem('sesionActiva');
  BotonCrearLista.style.display = 'block';
  verListas(sesion);
}else{
  BotonCrearLista.style.display = 'none';
}

BotonCrearLista.addEventListener('click', () => { modal.style.display = 'block'; });
BotoncerrarModal.addEventListener('click', () => { modal.style.display = 'none'; });

