import { useState, useEffect, useRef } from 'react';

const CharityForm = ({ onSubmit, editingCase, onCancel }) => {
  const [name, setName] = useState(editingCase?.name || '');
  const [totalAmount, setTotalAmount] = useState(editingCase?.totalAmount || '');
  const prevEditingCaseRef = useRef(null);

  useEffect(() => {
    if (editingCase && editingCase !== prevEditingCaseRef.current) {
      setName(editingCase.name);
      setTotalAmount(editingCase.totalAmount);
    } else if (!editingCase) {
      setName('');
      setTotalAmount('');
    }
    prevEditingCaseRef.current = editingCase;
  }, [editingCase]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const charityCase = {
      name,
      totalAmount: parseFloat(totalAmount)
    };
    if (editingCase) {
      charityCase.id = editingCase.id;
    }
    onSubmit(charityCase);
    if (!editingCase) {
      setName('');
      setTotalAmount('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="charity-form">
      <h2>{editingCase ? 'Editeaza Caz Caritabil' : 'Adauga Caz Caritabil'}</h2>
      <div className="form-group">
        <label htmlFor="name">Nume:</label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="totalAmount">Suma:</label>
        <input
          type="number"
          id="totalAmount"
          value={totalAmount}
          onChange={(e) => setTotalAmount(e.target.value)}
          step="0.01"
          min="0"
          required
        />
      </div>
      <div className="form-actions">
        <button type="submit">
          {editingCase ? 'Salveaza' : 'Adauga'}
        </button>
        {editingCase && (
          <button type="button" onClick={onCancel}>
            Anuleaza
          </button>
        )}
      </div>
    </form>
  );
};

CharityForm.displayName = 'CharityForm';
export default CharityForm;
