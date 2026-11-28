// SoporteYa · funciones comunes para el JavaScript de las páginas (Unidad 4)
'use strict';

const SY = (() => {
  // Lee el código CSRF que Thymeleaf dejó en las etiquetas <meta> del <head>
  function cabecerasCsrf() {
    const token = document.querySelector('meta[name="_csrf"]');
    const nombre = document.querySelector('meta[name="_csrf_header"]');
    return token && nombre ? { [nombre.content]: token.content } : {};
  }

  // GET que devuelve JSON. Si la sesión expiró, el servidor redirige al login: lo detectamos.
  async function get(url) {
    const respuesta = await fetch(url, { headers: { Accept: 'application/json' } });
    if (respuesta.redirected || !respuesta.ok) {
      throw new Error(respuesta.redirected ? 'Su sesión expiró. Vuelva a ingresar.' : `Error ${respuesta.status}`);
    }
    return respuesta.json();
  }

  // POST con el código CSRF. Si el servidor responde un ProblemDetail, lanzamos su mensaje.
  async function post(url) {
    const respuesta = await fetch(url, {
      method: 'POST',
      headers: { Accept: 'application/json', ...cabecerasCsrf() },
    });
    const datos = await respuesta.json().catch(() => ({}));
    if (!respuesta.ok) {
      throw new Error(datos.detail || `Error ${respuesta.status}`);
    }
    return datos;
  }

  // Un aviso flotante de Bootstrap (toast). textContent: nunca insertamos HTML que venga de datos.
  function aviso(mensaje, tipo = 'success') {
    const caja = document.createElement('div');
    caja.className = `toast align-items-center text-bg-${tipo} border-0`;
    caja.setAttribute('role', 'status');
    const cuerpo = document.createElement('div');
    cuerpo.className = 'toast-body';
    cuerpo.textContent = mensaje;
    caja.append(cuerpo);
    document.getElementById('avisos').append(caja);
    const toast = new bootstrap.Toast(caja, { delay: 4000 });
    caja.addEventListener('hidden.bs.toast', () => caja.remove());
    toast.show();
  }

  return { get, post, aviso };
})();

// Contador de tickets pendientes en el menú
(async () => {
  const badge = document.getElementById('contadorPendientes');
  if (!badge) return;
  try {
    const { pendientes } = await SY.get('/api/web/mis-pendientes');
    badge.textContent = pendientes;
    badge.hidden = pendientes === 0;
  } catch (e) {
    console.warn('No se pudo cargar el contador', e);
  }
})();