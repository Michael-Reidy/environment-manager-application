import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('LogicalLocation e2e test', () => {
  const logicalLocationPageUrl = '/logical-location-my-suffix';
  const logicalLocationPageUrlPattern = new RegExp('/logical-location-my-suffix(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const logicalLocationSample = {};

  let logicalLocation;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/logical-locations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/logical-locations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/logical-locations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (logicalLocation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/logical-locations/${logicalLocation.id}`,
      }).then(() => {
        logicalLocation = undefined;
      });
    }
  });

  it('LogicalLocations menu should load LogicalLocations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('logical-location-my-suffix');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LogicalLocation').should('exist');
    cy.url().should('match', logicalLocationPageUrlPattern);
  });

  describe('LogicalLocation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(logicalLocationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LogicalLocation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/logical-location-my-suffix/new$'));
        cy.getEntityCreateUpdateHeading('LogicalLocation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', logicalLocationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/logical-locations',
          body: logicalLocationSample,
        }).then(({ body }) => {
          logicalLocation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/logical-locations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/logical-locations?page=0&size=20>; rel="last",<http://localhost/api/logical-locations?page=0&size=20>; rel="first"',
              },
              body: [logicalLocation],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(logicalLocationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details LogicalLocation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('logicalLocation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', logicalLocationPageUrlPattern);
      });

      it('edit button click should load edit LogicalLocation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LogicalLocation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', logicalLocationPageUrlPattern);
      });

      it('edit button click should load edit LogicalLocation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LogicalLocation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', logicalLocationPageUrlPattern);
      });

      it('last delete button click should delete instance of LogicalLocation', () => {
        cy.intercept('GET', '/api/logical-locations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('logicalLocation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', logicalLocationPageUrlPattern);

        logicalLocation = undefined;
      });
    });
  });

  describe('new LogicalLocation page', () => {
    beforeEach(() => {
      cy.visit(`${logicalLocationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LogicalLocation');
    });

    it('should create an instance of LogicalLocation', () => {
      cy.get(`[data-cy="name"]`).type('Togo transmit').should('have.value', 'Togo transmit');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        logicalLocation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', logicalLocationPageUrlPattern);
    });
  });
});
