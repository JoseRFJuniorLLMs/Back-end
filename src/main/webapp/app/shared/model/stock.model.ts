export interface IStock {
    id?: number;
    symbol?: string;
    company?: string;
    bdr?: string;
    cnpj?: string;
    main_activity?: string;
    market_sector?: string;
    website?: string;
    activated?: boolean;
    marketSectorName?: string;
    marketSectorId?: number;
}

export class Stock implements IStock {
    constructor(
        public id?: number,
        public symbol?: string,
        public company?: string,
        public bdr?: string,
        public cnpj?: string,
        public main_activity?: string,
        public market_sector?: string,
        public website?: string,
        public activated?: boolean,
        public marketSectorName?: string,
        public marketSectorId?: number
    ) {
        this.activated = this.activated || false;
    }
}
