import { Component, Input } from '@angular/core';
import { RiskCell } from '../../../core/models/risk-cell.model';
import { RiskLevel } from '../../../core/models/risk-level';

@Component({
  selector: 'app-risk-details-panel',
  templateUrl: './risk-details-panel.component.html',
  styleUrl: './risk-details-panel.component.scss',
})
export class RiskDetailsPanelComponent {
  @Input() riskCell: RiskCell | null = null;

  protected levelClass(level: RiskLevel): string {
    return `level-${level.toLocaleLowerCase()}`;
  }
}
