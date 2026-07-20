import { Coordinate, RiskCell } from '../../../core/models/risk-cell.model';

function createHexagon(center: Coordinate): readonly Coordinate[] {
  const [latitude, longitude] = center;
  const latitudeRadius = 0.0105;
  const longitudeRadius = 0.0155;

  return Array.from({ length: 6 }, (_, index) => {
    const angle = (Math.PI / 180) * (60 * index + 30);
    return [
      latitude + latitudeRadius * Math.sin(angle),
      longitude + longitudeRadius * Math.cos(angle),
    ] as const;
  });
}

export const MOCK_RISK_CELLS: readonly RiskCell[] = [
  {
    id: 'central',
    name: 'Centru',
    score: 58,
    level: 'MODERATE',
    summary: 'Dense urban activity and elevated traffic create a balanced but watchful risk profile.',
    factors: [
      { label: 'Traffic density', value: 'Elevated' },
      { label: 'Surface temperature', value: '29.4 °C' },
      { label: 'Emergency access', value: 'Good' },
    ],
    polygon: createHexagon([46.7712, 23.5897]),
  },
  {
    id: 'marasti',
    name: 'Mărăști',
    score: 76,
    level: 'HIGH',
    summary: 'High traffic exposure and limited permeable surfaces increase heat and flash-flood sensitivity.',
    factors: [
      { label: 'Traffic density', value: 'High' },
      { label: 'Impervious surface', value: '82%' },
      { label: 'Heat exposure', value: 'High' },
    ],
    polygon: createHexagon([46.779, 23.618]),
  },
  {
    id: 'gheorgheni',
    name: 'Gheorgheni',
    score: 31,
    level: 'LOW',
    summary: 'Green corridors and good access to services keep the current local risk level low.',
    factors: [
      { label: 'Green coverage', value: '34%' },
      { label: 'Emergency access', value: 'Very good' },
      { label: 'Air quality', value: 'Good' },
    ],
    polygon: createHexagon([46.765, 23.625]),
  },
  {
    id: 'manastur',
    name: 'Mănăștur',
    score: 63,
    level: 'MODERATE',
    summary: 'Population density raises exposure, while nearby green areas partially offset urban heat risk.',
    factors: [
      { label: 'Population density', value: 'High' },
      { label: 'Green coverage', value: '21%' },
      { label: 'Slope exposure', value: 'Moderate' },
    ],
    polygon: createHexagon([46.756, 23.558]),
  },
  {
    id: 'grigorescu',
    name: 'Grigorescu',
    score: 48,
    level: 'MODERATE',
    summary: 'Proximity to the Someș river requires monitoring despite otherwise favorable urban conditions.',
    factors: [
      { label: 'River proximity', value: '320 m' },
      { label: 'Green coverage', value: '29%' },
      { label: 'Flood sensitivity', value: 'Moderate' },
    ],
    polygon: createHexagon([46.774, 23.555]),
  },
  {
    id: 'zorilor',
    name: 'Zorilor',
    score: 28,
    level: 'LOW',
    summary: 'Strong medical-service access and lower traffic pressure support a resilient local profile.',
    factors: [
      { label: 'Hospital access', value: 'Excellent' },
      { label: 'Traffic density', value: 'Low' },
      { label: 'Slope exposure', value: 'Moderate' },
    ],
    polygon: createHexagon([46.749, 23.59]),
  },
  {
    id: 'iris',
    name: 'Iris',
    score: 84,
    level: 'HIGH',
    summary: 'Industrial land use and sparse vegetation contribute to the highest combined exposure in the area.',
    factors: [
      { label: 'Industrial exposure', value: 'High' },
      { label: 'Green coverage', value: '9%' },
      { label: 'Air quality', value: 'Poor' },
    ],
    polygon: createHexagon([46.799, 23.593]),
  },
  {
    id: 'someseni',
    name: 'Someșeni',
    score: 91,
    level: 'HIGH',
    summary: 'Transport infrastructure, noise and limited shade create a concentrated high-risk zone.',
    factors: [
      { label: 'Transport exposure', value: 'Very high' },
      { label: 'Noise pressure', value: 'Elevated' },
      { label: 'Tree canopy', value: '7%' },
    ],
    polygon: createHexagon([46.786, 23.65]),
  },
  {
    id: 'andrei-muresanu',
    name: 'Andrei Mureșanu',
    score: 22,
    level: 'LOW',
    summary: 'Low building density and mature vegetation provide good protection against urban heat.',
    factors: [
      { label: 'Tree canopy', value: '41%' },
      { label: 'Building density', value: 'Low' },
      { label: 'Heat exposure', value: 'Low' },
    ],
    polygon: createHexagon([46.757, 23.61]),
  },
  {
    id: 'bulgaria',
    name: 'Bulgaria',
    score: 55,
    level: 'MODERATE',
    summary: 'Mixed residential and industrial activity keeps several environmental indicators under observation.',
    factors: [
      { label: 'Industrial exposure', value: 'Moderate' },
      { label: 'Air quality', value: 'Fair' },
      { label: 'Drainage capacity', value: 'Adequate' },
    ],
    polygon: createHexagon([46.794, 23.62]),
  },
];
