const CharityTable = ({ cases, onEdit, onDelete }) => {
  if (!cases || cases.length === 0) {
    return <p className="no-data">Nu există cazuri caritabile de afișat.</p>;
  }

  return (
    <table className="charity-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nume</th>
          <th>Sumă</th>
          <th>Acțiuni</th>
        </tr>
      </thead>
      <tbody>
        {cases.map((charityCase) => (
          <tr key={charityCase.id}>
            <td>{charityCase.id}</td>
            <td>{charityCase.name}</td>
            <td>{charityCase.totalAmount.toFixed(2)} RON</td>
            <td>
              <button
                className="edit-btn"
                onClick={() => onEdit(charityCase)}
                title="Editează"
              >
                ✏️
              </button>
              <button
                className="delete-btn"
                onClick={() => onDelete(charityCase.id)}
                title="Șterge"
              >
                🗑️
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default CharityTable;
