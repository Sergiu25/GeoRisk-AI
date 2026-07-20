import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  NgZone,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import * as L from 'leaflet';
import { Coordinate, RiskCell } from '../../../core/models/risk-cell.model';
import { RISK_COLORS } from '../../../shared/risk-presentation';

@Component({
  selector: 'app-risk-map',
  templateUrl: './risk-map.component.html',
  styleUrl: './risk-map.component.scss',
})
export class RiskMapComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input({ required: true }) riskCells: readonly RiskCell[] = [];
  @Input() selectedCellId: string | null = null;
  @Input() center: Coordinate = [46.7712, 23.6236];
  @Output() readonly cellSelected = new EventEmitter<RiskCell>();

  @ViewChild('mapContainer', { static: true }) private mapContainer!: ElementRef<HTMLElement>;

  private map?: L.Map;
  private readonly polygons = new Map<string, L.Polygon>();

  constructor(private readonly ngZone: NgZone) {}

  ngAfterViewInit(): void {
    this.map = L.map(this.mapContainer.nativeElement, {
      center: this.asLatLng(this.center),
      zoom: 13,
      zoomControl: false,
      attributionControl: true,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.map);

    L.control.zoom({ position: 'bottomleft' }).addTo(this.map);
    this.renderRiskCells();
    window.setTimeout(() => this.map?.invalidateSize(), 0);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['riskCells'] && this.map) {
      this.renderRiskCells();
    }

    if (changes['selectedCellId'] && this.map) {
      this.updateSelectionStyles();
    }

    if (changes['center'] && !changes['center'].firstChange && this.map) {
      this.map.flyTo(this.asLatLng(this.center), 13, { duration: 0.8 });
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private renderRiskCells(): void {
    if (!this.map) {
      return;
    }

    this.polygons.forEach((polygon) => polygon.remove());
    this.polygons.clear();

    this.riskCells.forEach((cell) => {
      const polygon = L.polygon(cell.polygon.map((point) => this.asLatLng(point)), this.styleFor(cell));

      polygon
        .bindTooltip(`${cell.name} · ${cell.score}`, {
          direction: 'top',
          className: 'risk-tooltip',
          offset: [0, -5],
        })
        .on('click', () => this.ngZone.run(() => this.cellSelected.emit(cell)))
        .addTo(this.map!);

      this.polygons.set(cell.id, polygon);
    });
  }

  private updateSelectionStyles(): void {
    this.riskCells.forEach((cell) => this.polygons.get(cell.id)?.setStyle(this.styleFor(cell)));
  }

  private styleFor(cell: RiskCell): L.PathOptions {
    const selected = cell.id === this.selectedCellId;
    return {
      color: selected ? '#173d2c' : '#ffffff',
      weight: selected ? 3 : 1.5,
      opacity: selected ? 1 : 0.9,
      fillColor: RISK_COLORS[cell.level],
      fillOpacity: selected ? 0.82 : 0.62,
    };
  }

  private asLatLng(coordinate: Coordinate): L.LatLngExpression {
    return [coordinate[0], coordinate[1]];
  }
}
