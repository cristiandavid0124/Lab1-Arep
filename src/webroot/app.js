document.getElementById('fetchDataBtn').addEventListener('click', function() {
    fetch('/api/jugadores')  // Ajusta esta URL si es necesario
        .then(response => response.text())  // Lee la respuesta como texto
        .then(text => {
            console.log('Response Text:', text);  // Imprime el texto de la respuesta
            try {
                const data = JSON.parse(text);
                // Extrae solo los nombres de los jugadores y usa HTML para formatear
                const nombres = data.map(jugador => `<div class="jugador-nombre">${jugador.jugador}</div>`).join('');
                document.getElementById('dataOutput').innerHTML = nombres;
            } catch (error) {
                console.error('Error parsing JSON:', error);
            }
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('addMessageBtn').addEventListener('click', function() {
    const jugador = document.getElementById('newMessage').value;  

    fetch('/api/jugador', {  
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            jugador: jugador  // Cambia 'Dinosaurio' a 'jugador'
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
            // Actualiza los datos después de agregar el mensaje
            document.getElementById('fetchDataBtn').click(); // Vuelve a cargar los datos
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('deleteMessageBtn').addEventListener('click', function() {
    const id = document.getElementById('deleteId').value;
    console.log('Deleting ID:', id);
    fetch(`/api/jugador/${id}`, {  
        method: 'DELETE',
    })
        .then(response => response.json())
        .then(data => {
            console.log('Deleted:', data);
            document.getElementById('fetchDataBtn').click(); // Vuelve a cargar los datos
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('updateMessageBtn').addEventListener('click', function() {
    const id = document.getElementById('updateId').value;
    const jugador = document.getElementById('NewJugador').value;  

    console.log('Updating ID:', id);
    fetch(`/api/jugador/${id}`, { 
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            jugador: jugador  
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log('Update:', data);
            document.getElementById('fetchDataBtn').click(); // Vuelve a cargar los datos
        })
        .catch(error => console.error('Error:', error));
});
